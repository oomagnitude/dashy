package com.oomagnitude.view

import org.scalajs.dom
import org.scalajs.dom.raw.UIEvent
import rx._

object Resize {
  val resize = Var(0)
  dom.onresize = {e: UIEvent => resize() = resize() + 1}
}
