package com.oomagnitude.rx.api

import com.oomagnitude.Uris._
import com.oomagnitude.api.{DataSourceFetchParams, DataSourceId}
import org.scalajs.dom.raw.{Event, MessageEvent, WebSocket}

object DataSourceWebSocket {
  val Connecting = 0 // The connection is not yet open.
  val Open = 1 // The connection is open and ready to communicate.
  val Closing = 2 // The connection is in the process of closing.
  val Closed = 3 //The connection is closed or couldn't be opened.
}

class DataSourceWebSocket(dataSource: DataSourceId, handleMessage: MessageEvent => Unit) {
  import DataSourceWebSocket._
  private[this] var webSocket: Option[WebSocket] = None


  def close(): Unit = {
    webSocket.foreach(_.close())
    webSocket = None
  }

  private def newWebSocket(): Unit = webSocket = Some(new WebSocket(dataSourceUrl(dataSource)))

  def open(): Unit = {
    if (webSocket.nonEmpty) {
      if (webSocket.get.readyState >= Closing) newWebSocket()
    } else newWebSocket()
  }

  def refreshParams(params: DataSourceFetchParams): Unit = {
    open()
    webSocket.foreach { ws =>
      ws.onopen = {e: Event => ws.send(upickle.write(params))}
      ws.onmessage = handleMessage
    }
  }
}
