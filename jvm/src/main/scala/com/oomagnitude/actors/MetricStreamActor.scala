package com.oomagnitude.actors

import akka.actor.{PoisonPill, Actor, Cancellable, Props}
import com.oomagnitude.actors.MetricStreamActor.StreamConfig
import com.oomagnitude.api.StreamControl._
import com.oomagnitude.api.{DataPoints, DataSourceId}
import upickle.Reader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object MetricStreamActor {
  case class StreamConfig(frequencyMillis: Int, resolution: Int)
  
  val DefaultBufferSize = 65536

  def props[T: Reader](dataSources: List[DataSourceId], config: StreamConfig)(implicit ec: ExecutionContext) =
    Props(classOf[MetricStreamActor[T]], dataSources, config, ec, implicitly[Reader[T]])

}

class MetricStreamActor[T](dataSources: List[DataSourceId], defaultConfig: StreamConfig, ec: ExecutionContext,
                           r: Reader[T]) extends Actor with Subscribable {
  implicit val executionContext = ec
  implicit val uReader = r

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
    context.system.scheduler.schedule(initialDelay = 0.seconds, interval = config.frequencyMillis.millis) {/*next()*/}
  }

  override def kill() = {
//    indexedFileReader ! Close

    // stopping the subscriber should result in the stream (and thus the connection) being closed
    subscriber.foreach(_ ! PoisonPill)
    context.stop(self)
  }

  val handleDataPoint: Receive = {
    case dataPoints: DataPoints[_] =>
      // TODO: type check that _ =:= T
      subscriber.foreach(_ ! dataPoints)
  }

  val handleControls: Receive = {
    case Pause =>
      pause()
    case Resume =>
      resume()
    case Next =>
//      next()
    case s: Seek =>
//      indexedFileReader ! s
    case Resolution(interval) =>
      config = config.copy(resolution = interval)
    case Frequency(duration: Int) =>
      config = config.copy(frequencyMillis = duration)
      if (running) { pause(); resume() }
  }

  override def receive: Receive = handleDataPoint orElse handleControls orElse handleSubsciption


}
