package com.oomagnitude.streams

import java.io.{BufferedReader, FileReader}
import java.nio.file.Path

import akka.actor._
import com.oomagnitude.api.DataPoint
import com.oomagnitude.api.StreamControl._
import com.oomagnitude.streams.FileStreamActor.Subscribe

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object FileStreamActor {
  val DefaultBufferSize = 65536

  def props(path: Path)(implicit ec: ExecutionContext) = Props(classOf[FileStreamActor], path, DefaultBufferSize, ec)

  case class Subscribe(subscriber: ActorRef)
  object terminated
}

class FileStreamActor(path: Path, bufferSize: Int, ec: ExecutionContext) extends Actor {
  implicit val executionContext = ec

  var subscriber: Option[ActorRef] = None
  var dataPointFrequency = 100.milliseconds
  var reader = open()
  var cancellable: Option[Cancellable] = Some(schedule)
  var mostRecentDataPoint = DataPoint.zero
  var timestepResolution = 1
  var paused = false

  private def open() = {
    new BufferedReader(new FileReader(path.toFile), bufferSize)
  }

  private def close() = {
    mostRecentDataPoint = DataPoint.zero
    reader.close()
  }

  private def reopen() = {
    close()
    reader = open()
  }

  private def kill() = {
    close()
    // stopping the subscriber should result in the stream (and thus the connection) being closed
    subscriber.foreach(_ ! PoisonPill)
    context.stop(self)
  }

  private def pause() = {
    paused = true
    cancellable.foreach(_.cancel())
    cancellable = None
  }

  private def resume() = {
    paused = false
    if (cancellable.isEmpty || cancellable.get.isCancelled) {
      cancellable = Some(schedule)
    }
  }

  private def schedule = {
    context.system.scheduler.schedule(initialDelay = 0.seconds, interval = dataPointFrequency) {
      if (subscriber.nonEmpty) {
        seek(mostRecentDataPoint.timestep + timestepResolution)
        subscriber.get ! mostRecentDataPoint
      }
    }
  }

  private def seek(timestep: Int): Unit = {
    if (timestep < mostRecentDataPoint.timestep) {
      // re-open the file (start at the beginning)
      reopen()
      seek(timestep)
    } else {
      val line = reader.readLine()
      if (line == null) kill()
      else {
        val dataPoint = upickle.read[DataPoint](line)
        if (dataPoint.timestep >= timestep) mostRecentDataPoint = dataPoint
        else seek(timestep)
      }
    }
  }

  override def receive: Receive = {
    case Pause => pause()
    case Resume => resume()
    case Seek(timestep) => seek(timestep)

    case Resolution(interval) =>
      timestepResolution = interval
    case Frequency(duration: Int) =>
      dataPointFrequency = duration.millis
      if (!paused) { pause(); resume() }
    case Subscribe(sub) =>
      // unwatch the current subscriber, if any
      subscriber.foreach(context.unwatch)
      // watch the new subscriber and save it
      context.watch(sub)
      subscriber = Some(sub)

    case Terminated(sub) =>
      if (subscriber.contains(sub)) {
        subscriber = None
        kill()
      }
    case FileStreamActor.terminated =>
      subscriber = None
      kill()
  }
}
