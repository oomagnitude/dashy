package com.oomagnitude.streams

import java.nio.file.Path

import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl._
import akka.stream.stage.{Context, PushStage, SyncDirective, TerminationDirective}
import akka.stream.{OverflowStrategy, UniformFanInShape}
import akka.util.ByteString
import com.oomagnitude.actors.{Subscribe, Close}
import com.oomagnitude.api.StreamControl.{Next, StreamControlMessage}
import com.oomagnitude.metrics.model.DataPoint
import upickle.Writer

import scala.concurrent.Future

object Flows {
  import scala.concurrent.duration._
  object Tick

  def lineByLineFile(path: Path): Source[ByteString, Future[Long]]#Repr[String, Future[Long]] = {
    SynchronousFileSource(path.toFile).transform(() => Stages.parseLines("\n"))
  }

  def reportErrorsFlow[T]: Flow[T, T, Unit] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
      def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

      override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
        println(s"WS stream failed with $cause")
        super.onUpstreamFailure(cause, ctx)
      }
    })

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

  def parseControlMessage = Flow[Message].map[StreamControlMessage] {
    case msg: TextMessage.Strict =>
      // TODO: expose or handle exceptions thrown here. Right now they are being swallowed.
      upickle.read[StreamControlMessage](msg.text)
    case msg: Message =>
      throw new UnsupportedOperationException(s"message $msg not supported")
  }

  def dataPointToMessage[T](implicit w: Writer[DataPoint[T]]) = Flow[DataPoint[T]].map { dataPoint =>
    // TODO: expose or handle exceptions thrown here. Right now they are being swallowed.
    val serialized = upickle.write(dataPoint)
    TextMessage.Strict(serialized)
  }

  def dynamicDataStreamFlow[T](fileStreamActor: ActorRef, bufferSize: Int)(implicit w: Writer[DataPoint[T]]): Flow[Message, Message, Any] = {
    Flow(Source.actorRef[DataPoint[T]](bufferSize, OverflowStrategy.fail)) {
      implicit builder =>
      { (responseSource) =>
        import FlowGraph.Implicits._

        // deserialize the incoming client messages into params objects
        val msgToObject = builder.add(parseControlMessage)
        // given individual data points, serialize them into ws messages
        val dataPointToMsg = builder.add(dataPointToMessage)

        // patches in 1. messages from client; 2. subscriber (downstream) that sends messages to client
        val merge = builder.add(Merge[Any](2))
        val dispatch = builder.add(Sink.actorRef(fileStreamActor, Close))

        // 0. inform dispatch actor of downstream actor that is subscribing
        // 1. send incoming ws messages to the dispatch actor
        // then: send the merged messages to the dispatch actor
        builder.matValue ~> Flow[ActorRef].map(Subscribe) ~> merge.in(0)
                                              msgToObject ~> merge.in(1)
                                                             merge ~> dispatch

        // convert response objects into ws messages
        // the actor that is materialized as the response source then subscribes to the output of the above graph
        responseSource ~> dataPointToMsg

        (msgToObject.inlet, dataPointToMsg.outlet)
      }
    }.mapMaterialized(_ ⇒ ())
  }

//  // 1. make one flow per file actor
//  def actorSource[T](actorRef: ActorRef)(implicit bufferSize: Int,
//                                         overflowStrategy: OverflowStrategy = OverflowStrategy.fail): Flow[Any, T, ActorRef] = {
//    Flow(Source.actorRef[T](bufferSize, overflowStrategy)) {
//      implicit builder => { (source) =>
//        import FlowGraph.Implicits._
//
//        val merge = builder.add(Merge[Any](2))
//        val actorSource = builder.add(Sink.actorRef(actorRef, Close))
//        val input = builder.add(Flow[Any])
//
//        // 1. source of flow's materialized value (i.e., the actor source)
//        // 2. source of incoming messages
//        // 3. merge #1 and #2 into one stream and direct it to the external actor
//        builder.matValue ~> Flow[ActorRef].map(Subscribe) ~> merge.in(0)
//                                                    input ~> merge.in(1)
//                                                             merge ~> actorSource
//
//        (input.inlet, source.outlet)
//      }
//    }
//  }
//
//  // 2. the for each flow, the outlets are merged into a map (key is DataSourceId, value is String)
//  // Merges a bunch of key/value pairs into a single map
//  def mergeToMap[K, V](numInlets: Int) = {
//    FlowGraph.partial() { implicit b =>
//      import FlowGraph.Implicits._
//
//      val zips = Vector.fill(numInlets) {
//        b.add(ZipWith[Map[K, V], (K, V), Map[K, V]] {
//          case (map, (key, value)) => map + (key -> value)
//        })
//      }
//
//      val seedFlow = b.add(Flow[Map[K, V]])
//      Source.repeat(Map.empty[K, V]) ~> seedFlow
//
//      val outlet = zips.foldLeft(seedFlow.outlet) {
//        (mapSource, zip) =>
//          mapSource ~> zip.in0
//          zip.out
//      }
//
//      UniformFanInShape(outlet, zips.map(_.in1): _*)
//    }
//  }
//
//  // 3. map that to the correct data type by deserializing w/ upickle
//  // 4. attach downstream processing, if needed (e.g., group and map)
//  // 5. sink to master control actor, which sends the message on its way

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
