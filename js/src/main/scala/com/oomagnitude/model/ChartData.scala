package com.oomagnitude.model

import com.oomagnitude.api.{DataPoints, DataSourceWebSocket}
import com.oomagnitude.metrics.model.{DataPoint, DataSourceId}
import com.oomagnitude.rx.CallbackRx
import org.scalajs.dom.raw.MessageEvent
import rx._
import upickle.Reader

import scala.concurrent.duration._

class ChartData[T](val params: ChartParams,
                   initiallyPaused: Boolean = false,
                   afterOpen: ChartData[T] => Unit = {d: ChartData[T] =>})(implicit reader: Reader[DataPoint[T]]) {
  private[this] val callbackRx = new CallbackRx({e: MessageEvent =>
    upickle.read[List[(DataSourceId, DataPoint[T])]](e.data.toString)},
    List.empty[(DataSourceId, DataPoint[T])])

  val webSocket = new DataSourceWebSocket(params.dataSources.map(_.id), callbackRx.callback,
    initiallyPaused, {e => afterOpen(this)})

  val signal: Rx[DataPoints[T]] = callbackRx.data

  private[this] val _clear = Var(true)
  val clearToggle: Rx[Boolean] = _clear

  val paused = Var(initiallyPaused)
  Obs(paused, skipInitial = true) {
    if (paused()) webSocket.pause()
    else webSocket.resume()
  }

  // TODO: reopen ws when seeking after end of data
  val location = Var(0)
  Obs(location, skipInitial = true) {
    doWhilePaused({() => webSocket.seek(location())})
  }

  val timestepResolution = Var(1)
  Obs(timestepResolution, skipInitial = true) {
    doWhilePaused({() => webSocket.resolution(timestepResolution())})
  }

  val frequency = Var(1000)
  Obs(frequency) {
    webSocket.frequency(frequency().millis)
  }

  def close(): Unit = {
    webSocket.close()
    callbackRx.close()
  }

  def next(): Unit = {
    webSocket.next()
  }

  private def emptyBuffers() = _clear() = !_clear()

  private def doWhilePaused(action: () => Unit): Unit = {
    if (paused()) {
      action(); emptyBuffers()
    } else {
      webSocket.pause()
      action(); emptyBuffers()
      webSocket.resume()
    }
  }
}
