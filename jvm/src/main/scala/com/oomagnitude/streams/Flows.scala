package com.oomagnitude.streams

import java.nio.file.Path

import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.OverflowStrategy
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl._
import akka.util.ByteString
import com.oomagnitude.api.{DataPoint, DataSourceFetchParams}
import com.oomagnitude.streams.StreamDispatch.Subscribe

import scala.concurrent.Future

object Flows {
  import scala.concurrent.duration._
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

  def parseFetchParams = Flow[Message].map[DataSourceFetchParams] {
    case msg: TextMessage.Strict =>
      // TODO: expose or handle exceptions thrown here. Right now they are being swallowed.
      upickle.read[DataSourceFetchParams](msg.text)
    case msg: Message =>
      throw new UnsupportedOperationException(s"message $msg not supported")
  }

  def dataPointToMessage = Flow[DataPoint].map { dataPoint =>
    // TODO: expose or handle exceptions thrown here. Right now they are being swallowed.
    val serialized = upickle.write(dataPoint)
    TextMessage.Strict(serialized)
  }

  def dynamicDataStreamFlow(dispatchRef: ActorRef, bufferSize: Int): Flow[Message, Message, Any] = {
    Flow(Source.actorRef[DataPoint](bufferSize, OverflowStrategy.fail)) {
      implicit builder =>
        { (responseSource) =>
          import FlowGraph.Implicits._

          // deserialize the incoming client messages into params objects
          val msgToParams = builder.add(parseFetchParams)
          // given individual data points, serialize them into ws messages
          val dataPointToMsg = builder.add(dataPointToMessage)

          // patches in 1. messages from client; 2. subscriber (downstream) that sends messages to client
          val merge = builder.add(Merge[Any](2))
          val dispatch = builder.add(Sink.actorRef(dispatchRef, StreamDispatch.terminated))

          // 0. inform dispatch actor of downstream actor that is subscribing
          // 1. send incoming ws messages to the dispatch actor
          // then: send the merged messages to the dispatch actor
          builder.matValue ~> Flow[ActorRef].map(Subscribe) ~> merge.in(0)
                                                msgToParams ~> merge.in(1)
                                                               merge ~> dispatch

          // convert response objects into ws messages
          // the actor that is materialized as the response source then subscribes to the output of the above graph
          responseSource ~> dataPointToMsg

          (msgToParams.inlet, dataPointToMsg.outlet)
        }
    }.mapMaterialized(_ ⇒ ())
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

  def fileSource(path: Path, params: DataSourceFetchParams): Source[DataPoint, _] = {
    // source that emits one string for every line in the file
    val fileSource = lineByLineFile(path)

    // source that emits every data point (message) that will be sent to the client
    var everyDataPoint = fileSource
      .map(upickle.read[DataPoint])
      // TODO: This will not work for metrics that are not published on every timestep.
      // TODO: Need to get metric metadata (start timestep, stop timestep, frequency) in order to fix.
      .filter(_.timestep % params.resolution == 0)

    if (params.timestepOffset.nonEmpty)
      everyDataPoint = everyDataPoint.filter(_.timestep >= params.timestepOffset.get)

    // source that will emit a data point no more often than requested by the client
    if (params.frequencySeconds.nonEmpty) {
      val dataPointFrequency = params.frequencySeconds.get.seconds
      everyDataPoint
        // immediately return all data points up to the initial batch size
        .take(params.initialBatchSize)
        // throttle all subsequent data points
        .concat(
          everyDataPoint
            .drop(params.initialBatchSize)
            .via(throttled(delay = 0.seconds, interval = dataPointFrequency)))
    } else everyDataPoint
  }
}
