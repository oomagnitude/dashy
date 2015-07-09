package com.oomagnitude.view

import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw.UIEvent
import rx._

object Resize {
  val resize = Var(0)
  dom.onresize = {e: UIEvent =>
    println(s"resize event")
//    resize() = resize() + 1
  }

  def widthOf(element: Element): Rx[Double] = {
    val width = Var(widthVal(element))

    dom.setInterval({ () =>
      val newWidth = widthVal(element)
      if (newWidth != width()) width() = newWidth
    }, 100)

    width
  }

  private def parseWidth(str: String): Double = {
    val pxIdx = str.indexOf("px")
    if (pxIdx >= 0) str.substring(0, pxIdx).trim.toDouble
    else 0.0
  }

  private def widthVal(element: Element): Double = parseWidth(dom.window.getComputedStyle(element, null).getPropertyValue("width"))

}
