package com.oomagnitude.rx

import org.scalajs.dom
import org.scalajs.dom.Node
import rx._

import scalatags.JsDom
import scalatags.JsDom.all._

object Rxs {

  implicit class RxOps[T](item: Rx[T])(implicit tToFrag: T => Frag) {
    def asFrag: Frag = asFrag({xs => span(xs).render})

    def asFrag(parent: (JsDom.Modifier*) => dom.Node): Frag = {
      def wrapped: dom.Node = parent(item())
      var last = wrapped
      Obs(item, skipInitial = true) {
        val newLast = wrapped
        last.parentNode.replaceChild(newLast, last)
        last = newLast
      }
      last
    }
  }

  implicit class RxListOps[T](items: Rx[List[T]])(implicit tToFrag: T => Frag) {

    def asFrags(postUpdate: Node => Unit = {n =>}): Frag = {
      def children = {
        // insert dummy div tag so that the parent is always accessible
        if (items().isEmpty) List(div().render)
        else items().map(_.render)
      }

      var last = children
      Obs(items, skipInitial = true) {
        val parent = last.head.parentNode
        val newLast = children
        last.foreach(parent.removeChild)
        newLast.foreach(parent.appendChild)
        last = newLast
        postUpdate(parent)
      }
      last
    }
  }


}