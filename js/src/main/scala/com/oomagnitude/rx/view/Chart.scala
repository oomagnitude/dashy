package com.oomagnitude.rx.view

import com.oomagnitude.rx.model.ChartData
import com.oomagnitude.rx.view.Charts.ChartType
import org.scalajs.dom.raw.{Event, MouseEvent}
import rx._

import scala.scalajs.js.JSON
import scalatags.JsDom.all._

class Chart(val chartType: ChartType, val data: ChartData, removeChart: (String) => Unit,
            val title: Option[String] = None) {
  import Charts._
  import Templates._

  val id = java.util.UUID.randomUUID.toString
  private[this] val removeButton = {
    val remove = bs.btnDefault(bootstrap.removeCircle).render
    remove.onclick = {e: MouseEvent =>  data.closed() = true; removeChart(id)}
    div(cls:="btn-group pull-right", role:="group", remove).render
  }
  private[this] val controls = {
    val frequencyInput = input(`type`:="text").render
    frequencyInput.onchange = {e: Event =>
      // TODO: need to get text of the element
      data.frequency() = frequencyInput.textContent.toInt
    }
    bs.formInline(bs.btnGroup(Dynamic.pauseButton(data.paused)), frequencyInput).render
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
