package com.oomagnitude.rx.view

import scala.scalajs.js

object Charts {
  val nv = js.Dynamic.global.nv
  val d3 = js.Dynamic.global.d3

  val DefaultHeight = 450
  // TODO: setting this to 100% results in availableWidth being NaN
  val DefaultWidth = 960
  val DefaultTransitionDuration = 500
  val DefaultX = {d: js.Dynamic => d.timestep}
  val DefaultY = {d: js.Dynamic => d.value}

  implicit class NvChartOps(chart: js.Dynamic) {
    def defaultOptions: js.Dynamic = {
      nv.utils.windowResize(chart.update)

      chart.margin(js.Dynamic.literal(top = 50, right = 20, bottom = 40, left = 55))
        .height(DefaultHeight)
        .width(DefaultWidth)
        .useInteractiveGuideline(true)
        .x(DefaultX)
        .y(DefaultY)
        .showLegend(true)
        .showXAxis(true)
        .showYAxis(true)

      nv.addGraph(js.Dynamic.literal(generate = {() => chart}))

      chart
    }
  }

  def defaultLineChart = {
    nv.models.lineChart().defaultOptions
  }

  def defaultLineWithFocusChart = {
    nv.models.lineWithFocusChart().defaultOptions
  }

  sealed trait ChartType {
    def emptyChart: js.Dynamic
  }
  case object Line extends ChartType {
    override def emptyChart: js.Dynamic = defaultLineChart
  }

}
