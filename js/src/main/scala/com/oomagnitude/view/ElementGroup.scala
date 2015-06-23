package com.oomagnitude.view

import com.oomagnitude.view.ElementGroup.CreateElement
import org.scalajs.dom.html
import rx._
object ElementGroup {
  type CreateElement[T] = (T, () => Unit) => html.Element
}

class ElementGroup[T](createElement: CreateElement[T]) {
  // Must have separate vars for item list and element list so that elements don't get re-generated
  private[this] val itemsWithId = Var(List.empty[(String, T)])
  private[this] val elementsWithId = Var(List.empty[(String, html.Element)])
  
  val items = Rx{itemsWithId().map(_._2)}
  val elements = Rx{elementsWithId().map(_._2)}

  def add(item: T): String = {
    val id = com.oomagnitude.rx.uuid()
    itemsWithId() = (id, item) :: itemsWithId()
    elementsWithId() = (id, createElement(item, {() => remove(id)})) :: elementsWithId()
    id
  }

  def remove(id: String): Unit = {
    elementsWithId() = elementsWithId().filterNot(_._1 == id)
    itemsWithId() = itemsWithId().filterNot(_._1 == id)
  }

  // TODO: also allow for additional remove operations on each item
  def clear(): Unit = {
    elementsWithId() = List.empty
    itemsWithId() = List.empty
  }
}