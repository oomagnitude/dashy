package com.oomagnitude.view

import com.oomagnitude.model.ChartData
import com.oomagnitude.view.Templates._
import org.scalajs.dom.raw.MouseEvent
import rx._

import scalatags.JsDom.all._

object ChartDataControls {
  def apply(data: ChartData[_]) = {
    val previousButton = bs.btnDefault("Previous", onclick:= {e: MouseEvent => data.previous()}).render
    val nextButton = bs.btnDefault("Next", onclick:= {e: MouseEvent => data.next()}).render
    val buttonGroup = bs.btnGroup(previousButton, nextButton).render

    Obs(data.paused) {
      val visibility = if (data.paused()) "visible" else "hidden"
      buttonGroup.style.visibility = visibility
    }

    bs.formHorizontal(bs.formGroup(
      bs.col1(bs.btnGroup(pauseButton(data.paused))),
      bs.col2(label("refresh millis")),
      bs.col2(numberInput(data.frequency)),
      bs.col1(label("seek to")),
      bs.col2(numberInput(data.location)),
      bs.col1(label("resolution")),
      bs.col2(numberInput(data.timestepResolution)),
      bs.col2(buttonGroup)))
  }
}
