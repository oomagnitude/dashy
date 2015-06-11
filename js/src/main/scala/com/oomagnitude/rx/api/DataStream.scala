package com.oomagnitude.rx.api

import org.scalajs.dom.WebSocket
import org.scalajs.dom.raw.MessageEvent
import rx._
import rx.ops._

import scala.concurrent.Promise

trait DataStream[T] {
  def data: Rx[T]

  def close(): Unit
}

class WebSocketDataStream[T](ws: WebSocket, convert: MessageEvent => T, initialValue: T) extends DataStream[T] {
  import scala.concurrent.ExecutionContext.Implicits.global
  private val p = Var(Promise[T]())

  ws.onmessage = { event: MessageEvent =>
    p().success(convert(event))
    p() = Promise[T]()
  }

  override val data: Rx[T] = Rx {p().future}.async(initialValue)

  override def close(): Unit = {
    ws.close()
    data.killAll()
  }
}

class DummyDataStream[T](raw: Rx[T]) extends DataStream[T] {
  override def data: Rx[T] = raw

  override def close(): Unit = raw.killAll()
}