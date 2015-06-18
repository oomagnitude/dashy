package com.oomagnitude.rx.model

import com.oomagnitude.api.{DataPoint, DataSourceId}
import com.oomagnitude.rx.api.{DataSourceWebSocket, Series}
import com.oomagnitude.rx.{Buffer, CallbackRx}
import org.scalajs.dom.raw.MessageEvent
import rx._

import scala.concurrent.duration._

class ChartData(val dataSources: List[DataSourceId]) {
  private[this] val rxs = dataSources.map {
    (_, new CallbackRx({e: MessageEvent => upickle.read[DataPoint](e.data.toString)}, DataPoint.zero))
  }.toMap

  private[this] val webSockets = dataSources.map { id =>
    (id, new DataSourceWebSocket(id, rxs(id).callback))
  }.toMap

  private[this] val buffers = rxs.map { case (id, stream) =>
    (id, new Buffer(stream.data))
  }

  val series = Rx{buffers.toList.map { case (id, buffer) =>
    Series(id.toString, buffer.data())
  }}

  val dataPointsPerSeries = Var(100)
  Obs(dataPointsPerSeries) {
    buffers.foreach(_._2.size = Some(dataPointsPerSeries()))
  }

  val paused = Var(false)
  Obs(paused, skipInitial = true) {
    if (paused()) webSockets.foreach(_._2.pause())
    else webSockets.foreach(_._2.resume())
  }

  // TODO: reopen ws when seeking after end of data
  val seekLocation = Var(0)
  Obs(seekLocation, skipInitial = true) {
    doWhilePaused({() => webSockets.foreach(_._2.seek(seekLocation()))})
  }

  val timestepResolution = Var(1)
  Obs(timestepResolution, skipInitial = true) {
    doWhilePaused({() => webSockets.foreach(_._2.resolution(timestepResolution()))})
  }

  val frequency = Var(1000)
  Obs(frequency) {
    webSockets.foreach(_._2.frequency(frequency().millis))
  }

  val closed = Var(false)
  Obs(closed, skipInitial = true) {
    if (closed()) {
      webSockets.foreach(_._2.close())
      rxs.foreach(_._2.close())
    }
  }

  val mode = Var("Sample")
  Obs(mode) {
    doWhilePaused{() => webSockets.foreach(_._2.mode(mode()))}
  }

  private def emptyBuffers() = buffers.foreach(_._2.data() = List.empty)

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
