package com.oomagnitude.view

import com.oomagnitude.model.ChartData
import org.scalajs.dom.raw.HTMLElement
import rx._

import scalatags.JsDom.all._

object TimeSeriesChart {
  val RickshawRenderers = List(
    SelectOption(id = "line", name = "line"),
    SelectOption(id = "stack", name = "stack"),
    SelectOption(id = "bar", name = "bar"),
    SelectOption(id = "scatterplot", name = "scatter"))

  def apply(data: ChartData[Double]): HTMLElement = {
    import Templates._

    val controls = ChartDataControls(data)

    val chartElement = bs.col12.render
    val legendElement = bs.col6.render
    val renderType = Var(RickshawRenderers.head.id)
    val radioElement = bs.col6(radio("renderer", RickshawRenderers, renderType))

    charts.defaultRickshawChart(chartElement, legendElement, renderType, data)

    div(bs.row(chartElement), bs.row(legendElement, radioElement), bs.row(bs.col12(controls))).render
  }
}