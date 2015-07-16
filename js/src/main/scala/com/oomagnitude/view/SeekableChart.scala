package com.oomagnitude.view

import com.oomagnitude.model.ChartData
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom
import scalatags.JsDom.all._

object SeekableChart {
  def apply(data: ChartData[_], chartBody: JsDom.Modifier): HTMLElement = {
    div(bs.row(chartBody), bs.row(bs.col12(ChartDataControls(data)))).render
  }

}
