package com.oomagnitude.dash.server.actors

import akka.actor.{Actor, ActorRef, Terminated}

trait Subscribable {
  this: Actor =>

  private[this] var _subscriber: Option[ActorRef] = None // subscriber - top-level actor

  def subscriber = _subscriber

  def kill(): Unit

  val handleSubsciption: Receive = {
    case Subscribe(sub) =>
      // unwatch the current subscriber, if any
      subscriber.foreach(context.unwatch)
      // watch the new subscriber and save it
      context.watch(sub)
      _subscriber = Some(sub)

    case Terminated(sub) =>
      if (subscriber.contains(sub)) {
        _subscriber = None
        kill()
      }
    case Close =>
      _subscriber = None
      kill()
  }
}
