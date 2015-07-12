package com.oomagnitude

package object js {
  implicit class DynamicExt(d: scalajs.js.Dynamic) {
    def toOption[T] = d.asInstanceOf[scalajs.js.UndefOr[T]].toOption
  }
}
