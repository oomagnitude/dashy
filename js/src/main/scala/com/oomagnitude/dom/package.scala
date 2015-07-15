package com.oomagnitude

import _root_.rx._
import d3.transition.D3Transition
import org.scalajs.dom.raw.{Event, SVGElement}
import org.scalajs.{dom => sdom}
import viz.transition.Transition
import scala.scalajs.{js => sjs}

package object dom {
  object all {

    import scalatags.JsDom.all._

    implicit class ElementOps[E <: sdom.Element](element: E) {
      def bindOption[T](attr: Attr, item: Rx[Option[T]]): E = {
        Obs(item) {
          item() match {
            case None => remove(attr)
            case Some(t) => set(attr, t)
          }
        }
        element
      }

      def bind[T](attr: Attr, item: Rx[T]): E = {
        Obs(item) {set(attr, item())}
        element
      }

      private def set[T](attr: Attr, value: T): Unit = element.setAttribute(attr.name, value.toString)
      private def remove[T](attr: Attr): Unit = element.removeAttribute(attr.name)

      def transition[T](datum: T): Transition = {
        new D3Transition(_root_.d3.d3.select(element).datum(datum.asInstanceOf[sjs.Any]).transition())
      }

    }

    implicit class SvgElementsOps[E <: SVGElement, T](elements: Seq[(E, T)]) {
      def onclick(click: (E, T) => Unit): Unit = {
        elements.foreach { case (element, datum) =>
          element.onclick = {_: Event => click(element, datum)}
        }
      }
      def onclick(click: T => Unit): Unit = {
        elements.foreach { case (element, datum) =>
          element.onclick = {_: Event => click(datum)}
        }
      }
      def onclick(click: (E, T, Int) => Unit): Unit = {
        elements.zipWithIndex.foreach { case ((element, datum), index) =>
          element.onclick = {_: Event => click(element, datum, index)}
        }
      }
    }
  }

}
