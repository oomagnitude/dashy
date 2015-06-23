package com.oomagnitude.view

import com.oomagnitude.view.ElementGroup._
import rx._

class RxListElementGroup[T](signal: Rx[List[T]], createElement: CreateElement[T]) {
  private[this] val group = new ElementGroup[T](createElement)
  Obs(signal) {group.clear(); signal().reverse.foreach(group.add)}

  val elements = group.elements
  val items = group.items
  def clear(): Unit = group.clear()
}
