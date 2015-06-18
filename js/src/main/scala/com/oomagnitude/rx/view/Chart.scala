package com.oomagnitude.rx.view

import com.oomagnitude.rx.model.ChartData
import com.oomagnitude.rx.view.Charts.ChartType
import org.scalajs.dom.raw.{Event, MouseEvent}
import rx._

import scalatags.JsDom.all._

class Chart(val chartType: ChartType, val data: ChartData, removeChart: (String) => Unit,
            val title: Option[String] = None) {
  import Charts._
  import Templates._

  val id = java.util.UUID.randomUUID.toString
  private[this] val removeButton = {
    val remove = bs.btnDefault(
      onclick:= {e: MouseEvent =>  data.closed() = true; removeChart(id)},
      bootstrap.removeCircle
    ).render
    div(cls:="btn-group pull-right", role:="group", remove).render
  }
  private[this] val controls = {
    val frequencyInput = input(`type`:="number").render
    frequencyInput.onchange = {(e: Event) => data.frequency() = frequencyInput.value.toInt
    }

    val seekInput = input(`type`:="number").render
    seekInput.onchange = {(e: Event) => data.seekLocation() = seekInput.value.toInt }

    val resolutionInput = input(`type`:="number").render
    resolutionInput.onchange = {(e: Event) => data.timestepResolution() = resolutionInput.value.toInt }

    bs.formHorizontal(bs.formGroup(
      bs.col1(bs.btnGroup(Dynamic.pauseButton(data.paused))),
      bs.col2(label("refresh millis")),
      bs.col2(frequencyInput),
      bs.col1(label("seek to")),
      bs.col2(seekInput),
      bs.col1(label("resolution")),
      bs.col2(resolutionInput),
      bs.col1(Dynamic.selectMenu(Rx{List(("Sample", "Sample"), ("Rate", "Rate"))}, data.mode)))).render
  }

  private[this] val chartElement = bootstrap.panelBody.render
  private[this] val chartPanel = {
    if (title.nonEmpty) {
      bootstrap.panel(bootstrap.panelHeading(h3(cls:="panel-title", title.get)), chartElement).render
    } else bootstrap.panel(chartElement).render
  }

  val container = bootstrap.row(bootstrap.col12(removeButton, chartPanel, controls))

  private[this] val chart = chartType.emptyChart
  private[this] val svg = d3.select(chartElement).append("svg")
    .attr("width", DefaultWidth)
    .attr("height", DefaultHeight)
    .append("g")
    .attr("transform", "translate(0,0)")

  Obs(data.series, skipInitial = true) {
    svg.datum(data.series().asJs)
      .transition()
      .duration(Charts.DefaultTransitionDuration)
      .call(chart)
  }
}
