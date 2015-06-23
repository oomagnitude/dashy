package com.oomagnitude.view

import com.oomagnitude.rx.model.ChartData
import org.scalajs.dom.raw.HTMLElement
import rx._

import scalatags.JsDom.all._

object TimeSeriesChart {
  val RickshawRenderers = List(("line", "line"), ("stack", "stack"), ("bar", "bar"),
    ("scatter", "scatterplot"))

  def apply(data: ChartData[Double]): HTMLElement = {
    import Templates._

    val controls = bs.formHorizontal(bs.formGroup(
      bs.col1(bs.btnGroup(pauseButton(data.paused))),
      bs.col2(label("refresh millis")),
      bs.col2(numberInput(data.frequency)),
      bs.col1(label("seek to")),
      bs.col2(numberInput(data.location)),
      bs.col1(label("resolution")),
      bs.col2(numberInput(data.timestepResolution)),
      bs.col1(selectMenu(Rx{List(("Sample", "Sample"), ("Rate", "Rate"))}, data.mode))))

    val chartElement = bs.col12.render
    val legendElement = bs.col6.render
    val renderType = Var(RickshawRenderers.head._2)
    val radioElement = bs.col6(radio("renderer", RickshawRenderers, renderType))

    charts.defaultRickshawChart(chartElement, legendElement, renderType, data.signals, data.clearToggle)

    div(bs.row(chartElement), bs.row(legendElement, radioElement), bs.row(bs.col12(controls))).render
  }
}