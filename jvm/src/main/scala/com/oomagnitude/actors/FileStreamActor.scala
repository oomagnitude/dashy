package com.oomagnitude.actors

import java.nio.file.Path

import akka.actor._
import com.oomagnitude.actors.IndexedFileReader.Advance
import com.oomagnitude.api.StreamControl._
import com.oomagnitude.metrics.model.DataPoint
import upickle.Reader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object FileStreamActor {
  val DefaultBufferSize = 65536

  def props[T: Reader](path: Path, paused: Boolean)(implicit ec: ExecutionContext) =
    Props(classOf[FileStreamActor[T]], path, DefaultBufferSize, paused, ec, implicitly[Reader[T]])
}

class FileStreamActor[T](path: Path, bufferSize: Int, initiallyPaused: Boolean, ec: ExecutionContext,
                         r: Reader[T]) extends Actor with Subscribable {
  implicit val executionContext = ec

  var dataPointFrequency = 100.milliseconds // frequency - top-level actor
  var cancellable: Option[Cancellable] = if (initiallyPaused) None else Some(schedule) // top-level actor
  var timestepResolution = 1 // top-level actor (tell it where to seek to)

  val indexedFileReader = context.actorOf(IndexedFileReader.props(path, bufferSize)())
  indexedFileReader ! Subscribe(self)

  def paused: Boolean = cancellable.isEmpty
  def running: Boolean = !paused

  override def kill() = {
    indexedFileReader ! Close

    // stopping the subscriber should result in the stream (and thus the connection) being closed
    subscriber.foreach(_ ! PoisonPill)
    context.stop(self)
  }

  private def pause() = {
    cancellable.foreach(_.cancel())
    cancellable = None
  }

  private def resume() = {
    if (cancellable.isEmpty || cancellable.get.isCancelled) {
      cancellable = Some(schedule)
    }
  }

  private def schedule = {
    context.system.scheduler.schedule(initialDelay = 0.seconds, interval = dataPointFrequency) {next()}
  }

  private def next(): Unit = {
    if (subscriber.nonEmpty) {
      indexedFileReader ! Advance(timestepResolution)
    }
  }

  val handleDataPoint: Receive = {
    case json: String =>
      implicit val uReader = r
      subscriber.foreach(_ ! upickle.read[DataPoint[T]](json))
  }

  val handleControls: Receive = {
    case Pause =>
      pause()
    case Resume =>
      resume()
    case Next =>
      next()
    case s: Seek =>
      indexedFileReader ! s
    case Resolution(interval) =>
      timestepResolution = interval
    case Frequency(duration: Int) =>
      dataPointFrequency = duration.millis
      if (running) { pause(); resume() }
  }

  override def receive: Receive = handleDataPoint orElse handleControls orElse handleSubsciption
}
