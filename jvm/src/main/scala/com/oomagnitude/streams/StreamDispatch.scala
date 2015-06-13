package com.oomagnitude.streams

import java.nio.file.Path

import akka.actor._
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.Sink
import com.oomagnitude.api.{DataPoint, DataSourceFetchParams}
import com.oomagnitude.streams.StreamDispatch.Subscribe

object StreamDispatch {
  object terminated
  case class Subscribe(actorRef: ActorRef)
}

class StreamDispatch(path: Path, fm: FlowMaterializer) extends Actor {
  implicit val f = fm

  // the subscriber is the actor that can send individual messages back to the client
  var subscriber: Option[ActorRef] = None
  var params: Option[DataSourceFetchParams] = None

  override def receive: Receive = {
    case newParams: DataSourceFetchParams =>
      params = Some(newParams)
      // update params and apply changes to the stream
      refreshStream()

    case Subscribe(sub) =>
      // unwatch the current subscriber, if any
      subscriber.foreach(context.unwatch)
      // watch the new subscriber and save it
      context.watch(sub)
      subscriber = Some(sub)
      // refresh the stream to flow to the new subscriber
      refreshStream()

    case Terminated(sub) =>
      if (subscriber.contains(sub)) {
        subscriber = None
        // if the subscriber is terminated, then the stream must be cancelled
        // terminate this actor, because it has no purpose anymore
        context.stop(self)
      }
    case StreamDispatch.terminated =>
      cancelStream()
      context.stop(self)

  }

  private def cancelStream(): Unit = {
    context.children.foreach { child =>
      context.unwatch(child)
      context.stop(child)
    }
  }
  
  private def refreshStream(): Unit = {
    // kill all children. The effect of doing this is to close the flow between this actor and the client
    // (which also closes the underlying file)
    cancelStream()

    subscriber.foreach { sub =>
      params.foreach { p =>
        val source = Flows.fileSource(path, p)

        val passThrough = context.actorOf(Props(new Actor{
          override def receive: Actor.Receive = {
            case StreamDispatch.terminated =>
              // TODO: better or more graceful way to terminate the downstream?
              sub ! PoisonPill
            case msg =>
              sub ! msg
          }
        }))
        source.runWith(Sink.actorRef(passThrough, StreamDispatch.terminated))
      }
    }
  }

}
