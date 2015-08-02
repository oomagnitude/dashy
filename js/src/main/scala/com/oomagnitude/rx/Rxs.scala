package com.oomagnitude.rx

import org.scalajs.dom
import rx._

import scala.concurrent.Future
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
    def asFrags(postAppend: dom.Node => Unit): Frag = {
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
        newLast.foreach{newElem => parent.appendChild(newElem); postAppend(newElem)}
        last = newLast
      }
      last
    }

    def asFrags: Frag = asFrags({n: dom.Node =>})
  }

  private class Counter {
    private[this] var count = 0
    def increment(): Int = {
      count += 1; count
    }
    def isCurrent(value: Int) = value == count

  }

  def fetchOnChange[T,V](observed: Rx[Option[T]], items: Var[List[V]], fetch: T => Future[List[V]]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val counter = new Counter
    Obs(observed) {
      val c = counter.increment()
      items() = List.empty

      observed().foreach {
        fetch(_).onSuccess { case r =>
          // Only update if this is the latest change
          if (counter.isCurrent(c)) items() = r
        }
      }
    }
  }
}
