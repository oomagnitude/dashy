package com.oomagnitude.rx.model

import com.oomagnitude.api.{DataPoint, DataSourceId}
import com.oomagnitude.rx.CallbackRx
import com.oomagnitude.rx.api.DataSourceWebSocket
import org.scalajs.dom.raw.MessageEvent
import rx._
import upickle.Reader

import scala.concurrent.duration._

class ChartData[T](val dataSources: List[DataSourceId], zero: T, initiallyPaused: Boolean = false,
                   afterOpen: ChartData[T] => Unit = {d: ChartData[T] =>})(implicit reader: Reader[DataPoint[T]]) {
  private[this] val rxs = dataSources.map {
    (_, new CallbackRx({e: MessageEvent => upickle.read[DataPoint[T]](e.data.toString)}, DataPoint.zero(zero)))
  }.toMap

   val webSockets = dataSources.map { id =>
    (id, new DataSourceWebSocket(id, rxs(id).callback, initiallyPaused, {e => afterOpen(this)}))
  }.toMap

  val signals: List[(DataSourceId, Rx[DataPoint[T]])] = rxs.toList.map(kv => (kv._1, kv._2.data))

  private[this] val _clear = Var(true)
  val clearToggle: Rx[Boolean] = _clear

  val paused = Var(initiallyPaused)
  Obs(paused, skipInitial = true) {
    if (paused()) webSockets.foreach(_._2.pause())
    else webSockets.foreach(_._2.resume())
  }

  // TODO: reopen ws when seeking after end of data
  val location = Var(0)
  Obs(location, skipInitial = true) {
    doWhilePaused({() => webSockets.foreach(_._2.seek(location()))})
  }

  val timestepResolution = Var(1)
  Obs(timestepResolution, skipInitial = true) {
    doWhilePaused({() => webSockets.foreach(_._2.resolution(timestepResolution()))})
  }

  val frequency = Var(1000)
  Obs(frequency) {
    webSockets.foreach(_._2.frequency(frequency().millis))
  }

  def close(): Unit = {
    webSockets.foreach(_._2.close())
    rxs.foreach(_._2.close())
  }

  def next(): Unit = {
    webSockets.foreach(_._2.next())
  }

  val mode = Var("Sample")
  Obs(mode) {
    doWhilePaused{() => webSockets.foreach(_._2.mode(mode()))}
  }

  private def emptyBuffers() = _clear() = !_clear()

  private def doWhilePaused(action: () => Unit): Unit = {
    if (paused()) {
      action(); emptyBuffers()
    } else {
      webSockets.foreach(_._2.pause())
      action(); emptyBuffers()
      webSockets.foreach(_._2.resume())
    }
  }
}
