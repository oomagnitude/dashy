package com.oomagnitude.dash.server.actors

import java.nio.file.Path

import akka.actor.{Actor, Cancellable, PoisonPill, Props}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink}
import com.oomagnitude.dash.server.actors.IndexedFileReader.Advance
import com.oomagnitude.dash.server.actors.MultiplexFileController._
import com.oomagnitude.api.StreamControl._
import com.oomagnitude.dash.server.streams.Flows

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object MultiplexFileController {
  case class StreamSource[K, V](key: K, path: Path, transform: Flow[String, V, Any])
  case class StreamConfig(frequencyMillis: Int, resolution: Int)
  
  val DefaultBufferSize = 65536

  def props[K, V](sources: Iterable[StreamSource[K, V]], config: StreamConfig)(
      implicit ec: ExecutionContext, fm: Materializer) =
    Props(classOf[MultiplexFileController[K, V]], sources, config, ec, fm)
}

class MultiplexFileController[K, V](sources: Iterable[StreamSource[K, V]], defaultConfig: StreamConfig,
                           ec: ExecutionContext, fm: Materializer) extends Actor with Subscribable {
  implicit val executionContext = ec
  implicit val bufferSize = 100
  implicit val flowMaterializer = fm

  val dataFlowIndex = sources.map(df => df.key -> df).toMap
  
  val fileActors = sources.map { f =>
    (f.key, context.actorOf(IndexedFileReader.props(f.path, DefaultBufferSize)()))
  }
  
  val metricFlow = Flows.mergedSources(fileActors.map {
    case (id, actorRef) => (id, Flows.actorSource[String](actorRef).via(dataFlowIndex(id).transform))
  }).to(Sink.actorRef(self, Close)).run()

  var config = defaultConfig
  var cancellable: Option[Cancellable] = None

  def paused: Boolean = cancellable.isEmpty
  def running: Boolean = !paused

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
    context.system.scheduler.schedule(initialDelay = 0.seconds, interval = config.frequencyMillis.millis) {next()}
  }

  override def kill() = {
    // stopping the subscriber should result in the stream (and thus the connection) being closed
    subscriber.foreach(_ ! PoisonPill)
    context.stop(self)
  }

  private def next(): Unit = {
    if (subscriber.nonEmpty) {
      fileActors.foreach { case (id, actorRef) =>
        actorRef ! Advance(config.resolution)
      }
    }
  }

  val handleDataPoint: Receive = {
    case dataPoints: Iterable[_] =>
      // TODO: type check that _ =:= (K, V)
      // TODO: pass along all data points eventually
      subscriber.foreach(_ ! dataPoints)
  }

  val handleControls: Receive = {
    case Pause =>
      pause()
    case Resume => // same as Start
      resume()
    case Next =>
      next()
    case s: Seek =>
      fileActors.foreach { case (id, actorRef) =>
        actorRef ! s
      }
    case Resolution(interval) =>
      config = config.copy(resolution = interval)
    case Frequency(duration: Int) =>
      config = config.copy(frequencyMillis = duration)
      if (running) { pause(); resume() }
  }

  override def receive: Receive = handleDataPoint orElse handleControls orElse handleSubsciption

}
