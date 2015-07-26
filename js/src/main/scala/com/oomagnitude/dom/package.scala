package com.oomagnitude

import _root_.rx._
import org.scalajs.{dom => sdom}

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
    }
  }

}
