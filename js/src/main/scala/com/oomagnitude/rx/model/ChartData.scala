package com.oomagnitude.rx.model

import com.oomagnitude.api.{DataPoint, DataSourceFetchParams, DataSourceId}
import com.oomagnitude.rx.api.{DataSourceWebSocket, Series}
import com.oomagnitude.rx.{Buffer, CallbackRx}
import org.scalajs.dom.raw.MessageEvent
import rx._

class ChartData(val dataSources: List[DataSourceId], val params: Var[DataSourceFetchParams]) {
  private[this] val rxs = dataSources.map {
    (_, new CallbackRx({e: MessageEvent => upickle.read[DataPoint](e.data.toString)}, DataPoint.zero))
  }.toMap

  private[this] val webSockets = dataSources.map { id =>
    (id, new DataSourceWebSocket(id, rxs(id).callback))
  }.toMap

  private[this] val buffers = rxs.map { case (id, stream) =>
    (id, new Buffer(stream.data))
  }

  Obs(params) {
    buffers.foreach(_._2.size = Some(params().initialBatchSize))
    webSockets.foreach(_._2.refreshParams(params()))
  }

  val series = Rx{buffers.toList.map { case (id, buffer) =>
    Series(id.toString, buffer.data())
  }}
  var open = true

  def resume(): Unit = {
    webSockets.foreach { case (id, ws) =>
      ws.refreshParams(params().copy(initialBatchSize = 0, timestepOffset = latestTimestep(id)))
    }
  }

  private def latestTimestep(id: DataSourceId) = buffers(id).data().lastOption.map(_.timestep.toLong)

  def pause(): Unit = {
    webSockets.foreach(_._2.close())
  }

  def close(): Unit = {
    pause()
    if (open) {
      rxs.foreach(_._2.close())
      open = false
    }
  }
}
