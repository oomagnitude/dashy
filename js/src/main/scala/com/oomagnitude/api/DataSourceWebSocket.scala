package com.oomagnitude.api

import com.oomagnitude.api.StreamControl._
import com.oomagnitude.api.Uris._
import com.oomagnitude.metrics.model.DataSourceId
import org.scalajs.dom.Event
import org.scalajs.dom.raw.{MessageEvent, WebSocket}

import scala.concurrent.duration.Duration

object DataSourceWebSocket {
  val Connecting = 0 // The connection is not yet open.
  val Open = 1 // The connection is open and ready to communicate.
  val Closing = 2 // The connection is in the process of closing.
  val Closed = 3 //The connection is closed or couldn't be opened.
}

class DataSourceWebSocket(dataSources: List[DataSourceId],
                          handleMessage: MessageEvent => Unit,
                          paused: Boolean = false,
                          afterOpen: Event => Unit = {e => }) {
  import DataSourceWebSocket._
  private[this] val webSocket = new WebSocket(dataSourceUrl(dataSources, paused))
  webSocket.onmessage = handleMessage
  webSocket.onopen = afterOpen

  def close() = webSocket.close()

  def pause() = send(Pause)

  def resume() = send(Resume)

  def next() = send(Next)

  def start() = send(Start)

  def seek(timestep: Int) = send(Seek(timestep))

  def resolution(timestepInterval: Int) = send(Resolution(timestepInterval))

  def frequency(duration: Duration) = send(Frequency(duration.toMillis.toInt))

  private def send(message: StreamControlMessage): Unit = {
    if (webSocket.readyState == Open) {
      webSocket.send(StreamControl.write(message))
    }
  }
}
