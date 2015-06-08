package com.oomagnitude.streams

import java.nio.file.Path

import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object Flows {
  object Tick

  def lineByLineFile(path: Path): Source[ByteString, Future[Long]]#Repr[String, Future[Long]] = {
    SynchronousFileSource(path.toFile).transform(() => Stages.parseLines("\n"))
  }

  /**
   * Ignores all incoming messages from the client. Grafts a server-side message source into the flow. All messages
   * from this source are then emitted to the client via this flow.
   *
   * @param serverMessageSource the source of messages, originating from this server
   * @return the flow
   */
  def serverToClientMessageFlow(serverMessageSource: Source[String, _]): Flow[Message, Message, Any] = {
    Flow() { implicit builder =>
      import FlowGraph.Implicits._

      val sink = builder.add(Sink.ignore)
      val mapStringToMsg = builder.add(Flow[String].map[Message]{TextMessage.Strict})

      val source = builder.add(serverMessageSource)

      source ~> mapStringToMsg

      (sink.inlet, mapStringToMsg.outlet)
    }
  }

  /**
   * Throttles all messages passing through it by only allowing no more than one message at each regular interval
   *
   * @param delay the delay before the first message is set
   * @param interval the interval between successive messages
   * @tparam T the type of message
   * @return a source that has been throttled
   */
  def throttled[T](delay: FiniteDuration, interval: FiniteDuration): Flow[T, T, _] = {
    Flow() { implicit builder =>
      import FlowGraph.Implicits._

      val tickSource = Source(delay, interval, Tick)
      // we use zip to throttle the stream
      val zip = builder.add(Zip[Tick.type, T]())
      val unzip = builder.add(Flow[(Tick.type, T)].map(_._2))

      // setup the message flow
      tickSource ~> zip.in0
      zip.out ~> unzip

      (zip.in1, unzip.outlet)
    }
  }

  /**
   * Emits every nth message it receives
   *
   * @param n how often to emit a message (i.e., one is emitted for every n received from upstream)
   * @tparam T the type of message
   * @return the flow
   */
  def everyN[T](n: Int): Flow[T, T, _] = {
    Flow() { implicit builder =>
      import FlowGraph.Implicits._

      // acts as an index for the stream so that only every n elements is emitted
      val index = builder.add(Source(() => Iterator.from(0)))
      // zip together the index with the original stream
      val zip = builder.add(Zip[Int, T]())
      // filter out all but every n elements
      val filter = builder.add(Flow[(Int, T)].filter(_._1 % n == 0))
      // remove the index
      val unzip = builder.add(Flow[(Int, T)].map(_._2))

      index ~> zip.in0
      zip.out ~> filter ~> unzip

      (zip.in1, unzip.outlet)
    }
  }
}
