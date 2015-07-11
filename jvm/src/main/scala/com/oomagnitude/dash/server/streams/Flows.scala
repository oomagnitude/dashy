package com.oomagnitude.dash.server.streams

import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl._
import akka.stream.stage.{Context, PushStage, SyncDirective, TerminationDirective}
import akka.stream.{OverflowStrategy, UniformFanInShape}
import com.oomagnitude.api.StreamControl.StreamControlMessage
import com.oomagnitude.api.{StreamControl, DataPoints, JsValues}
import com.oomagnitude.dash.server.actors.{Close, Subscribe}
import com.oomagnitude.metrics.model.DataPoint
import com.oomagnitude.metrics.model.Metrics.{Count, Time}
import upickle._
import upickle.default._

object Flows {
  import scala.concurrent.duration._
  object Tick

  def reportErrorsFlow[T]: Flow[T, T, Unit] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
      def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

      override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
        println(s"WS stream failed with $cause")
        super.onUpstreamFailure(cause, ctx)
      }
    })


  def parseControlMessage = Flow[Message].map[StreamControlMessage] {
    case msg: TextMessage.Strict =>
      // TODO: expose or handle exceptions thrown here. Right now they are being swallowed.
      StreamControl.read(msg.text)
    case msg: Message =>
      throw new UnsupportedOperationException(s"message $msg not supported")
  }

  def serializeDataPoint[T: Writer] = Flow[DataPoints[T]].map(dp => write(dp))

  def jsValuesToMessage = Flow[JsValues].map { jsValues =>
    val js = Js.Arr(jsValues.toSeq.map {
      case (id, jsval) => Js.Arr(writeJs(id), jsval)
    }: _*)
    upickle.json.write(js)
  }

  def stringToMessage = Flow[String].map(TextMessage.Strict)

  def dataPointMessageFlow[T: Writer](actorRef: ActorRef)(implicit bufferSize: Int = 100) = Flow[Message]
    .via(parseControlMessage)
    .via(actorFlow[DataPoints[T]](actorRef))
    .via(serializeDataPoint[T])
    .via(stringToMessage)

  def untypedMessageFlow(actorRef: ActorRef)(implicit bufferSize: Int = 100) = Flow[Message]
    .via(parseControlMessage)
    .via(actorFlow[JsValues](actorRef))
    .via(jsValuesToMessage)
    .via(stringToMessage)

  def actorSource[Out](actorRef: ActorRef)(implicit bufferSize: Int,
                                         overflowStrategy: OverflowStrategy = OverflowStrategy.fail) = Source() { implicit b =>
    import FlowGraph.Implicits._
    val actorRefFlow = b.add(actorFlow[Out](actorRef))
    val emptySource = b.add(Source.empty[Any])
    emptySource ~> actorRefFlow
    actorRefFlow.outlet
  }

  def actorFlow[Out](actorRef: ActorRef)(implicit bufferSize: Int,
                                             overflowStrategy: OverflowStrategy = OverflowStrategy.fail): Flow[Any, Out, Any] =
    Flow(Source.actorRef[Out](bufferSize, OverflowStrategy.fail)) {
      implicit b => { (responseSource) =>
        import FlowGraph.Implicits._
        val merge = b.add(Merge[Any](2))
        val dispatch = b.add(Sink.actorRef(actorRef, Close))

        // 1. source of flow's materialized value (i.e., the actor source)
        // 2. source of incoming messages
        // 3. merge #1 and #2 into one stream and direct it to the external actor
        b.materializedValue ~> Flow[ActorRef].map(Subscribe) ~> merge.in(0)
                                                       merge ~> dispatch

        (merge.in(1), responseSource.outlet)
      }
  }

  // 2. the for each flow, the outlets are merged into a map (key is DataSourceId, value is String)
  // Merges a bunch of key/value pairs into a single map
  def mergeToList[T](numInlets: Int) = {
    FlowGraph.partial() { implicit b =>
      import FlowGraph.Implicits._

      val zips = Vector.fill(numInlets) {
        b.add(ZipWith[List[T], T, List[T]] {
          case (map, item) => item :: map
        })
      }

      val seedFlow = b.add(Flow[List[T]])
      Source.repeat(List.empty[T]) ~> seedFlow

      val outlet = zips.foldLeft(seedFlow.outlet) {
        (mapSource, zip) =>
          mapSource ~> zip.in0
          zip.out
      }

      UniformFanInShape(outlet, zips.map(_.in1): _*)
    }
  }

  def mergedSources[K, V](outlets: Iterable[(K, Source[V, _])])(implicit bufferSize: Int,
                                           overflowStrategy: OverflowStrategy = OverflowStrategy.fail) = {
    Source() { implicit b =>
      import FlowGraph.Implicits._

      val sources = outlets.map {
        case (key, outlet) =>
          val zip = b.add(Zip[K, V]())
          Source.repeat(key) ~> zip.in0
          outlet ~> zip.in1
          zip.out
      }

      val merge = b.add(mergeToList[(K, V)](sources.size))
      sources.zipWithIndex.foreach {
        case (dataSource, i) => dataSource ~> merge.in(i)
      }

      merge.out
    }
  }

  def parseJson[T: Reader] = Flow[String].map(json => read[T](json))

  def parseJs = Flow[String].map(json => upickle.json.read(json))

  def timerFlow = Flow[DataPoint[Time]].transform(() => SlidingWindow[DataPoint[Time]](2)).collect {
    case samples if samples.size == 2 =>
      val (first, second) =
        if (samples.head.timestep < samples(1).timestep) (samples.head, samples(1))
        else (samples(1), samples.head)
      val elapsed = (second.value.elapsed - first.value.elapsed) / (second.value.count - first.value.count)
      DataPoint(second.timestep, elapsed)
  }

  def counterFlow = Flow[DataPoint[Count]].transform(() => SlidingWindow[DataPoint[Count]](2)).collect {
    case samples if samples.size == 2 =>
      val (first, second) =
        if (samples.head.timestep < samples(1).timestep) (samples.head, samples(1))
        else (samples(1), samples.head)
      val rate = (second.value.count - first.value.count) / (second.timestep - first.timestep).toDouble
      DataPoint(second.timestep, rate)
  }

  def toJs[T: Writer] = Flow[T].map(t => writeJs(t))

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
