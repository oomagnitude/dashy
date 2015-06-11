package com.oomagnitude.rx.view

import com.oomagnitude.rx.api.Series
import nvd3.Margin
import org.scalajs.dom.html
import rx._

import scala.scalajs.js

object Charts {
  val nv = js.Dynamic.global.nv
  val d3 = js.Dynamic.global.d3

  val DefaultMargin = Margin.create(top = 20, right = 20, bottom = 40, left = 55)
  val DefaultHeight = 450
  // TODO: setting this to 100% results in availableWidth being NaN
  val DefaultWidth = 960
  val DefaultTransitionDuration = 500
  val DefaultX = {d: js.Dynamic => d.timestep}
  val DefaultY = {d: js.Dynamic => d.value}

  implicit class ChartOps(chart: js.Dynamic) {
    def defaultOptions: js.Dynamic = {
      nv.utils.windowResize(chart.update)

      chart.margin(DefaultMargin.asJs)
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

  implicit class ContainerOps(container: html.Div) {
    def addChart(chart: js.Dynamic, data: Rx[List[Series]]): html.Div = {
      val svg = d3.select(container).append("svg")
        .attr("width", DefaultWidth)
        .attr("height", DefaultHeight)
        .append("g")
        .attr("transform", "translate(0,0)")

      Obs(data, skipInitial = true) {
        loadGraph(data().asJs, chart, svg)
      }
      container
    }
  }

  def defaultLineChart = {
    nv.models.lineChart().defaultOptions
  }

  def defaultLineWithFocusChart = {
    nv.models.lineWithFocusChart().defaultOptions
  }

  private def loadGraph(data: js.Dynamic, chart: js.Dynamic, svg: js.Dynamic): Unit = {
    svg.datum(data)
      .transition()
      .duration(500)
      .call(chart)
  }

}
