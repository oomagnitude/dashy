package com.oomagnitude.view

import com.oomagnitude.rx.Rxs._
import com.oomagnitude.view.ElementGroup.CreateElement
import rx._
import scalatags.JsDom.all._

class RxOptionElementGroup[T](signal: Rx[Option[T]], createElement: CreateElement[T]) {
  private[this] val group = new ElementGroup[T](createElement)
  Obs(signal) {signal().foreach(group.add)}

  val element = div(group.elements.asFrags()).render
  val items = group.items
  def clear(): Unit = group.clear()
}
