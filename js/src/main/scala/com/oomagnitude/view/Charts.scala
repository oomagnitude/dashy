package com.oomagnitude.view

import com.oomagnitude.model.ChartData
import org.scalajs.dom.html
import rx._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l, newInstance => jsnew}

object Charts {
  val DefaultDataPointsPerSeries = 100

  def defaultRickshawChart(chartContainer: html.Element, legendContainer: html.Element, renderMode: Rx[String],
                           data: ChartData[Double]): js.Dynamic = {
    val series = new js.Array[js.Dynamic]()
    val palette = jsnew(Rickshaw.Color.Palette)()
    val arrays = data.params.dataSources.map {
      m =>
        val array = new js.Array[js.Dynamic]()

        // TODO: observer for clear buffer
        series.push(l(name = m.id.toString, data = array, color = palette.color()))
        m.id -> array
    }.toMap

    val graph = jsnew(Rickshaw.Graph)(l(element = chartContainer, renderer = "line", series = series,
      width = DefaultWidth, height = DefaultHeight))

    Obs(data.signal) {
      data.signal().foreach {
        case (id, dataPoint) =>
          arrays.get(id).foreach { array =>
            array.push(l(x = dataPoint.timestep, y = dataPoint.value))
            if (array.length > DefaultDataPointsPerSeries) { array.shift() }
          }
      }
      graph.update()
    }

    Obs(data.clearToggle) {
      arrays.foreach(_._2.length = 0)
      graph.update()
    }

    val xAxis = jsnew(Rickshaw.Graph.Axis.X)(l(graph = graph))
    val yAxis = jsnew(Rickshaw.Graph.Axis.Y)(l(graph = graph))
    val legend = jsnew(Rickshaw.Graph.Legend)(l(element = legendContainer, graph = graph ))

    val timestepDisplay: js.Function1[js.Any, String] = {(t: js.Any) => "timestep " + t.toString}
    val shelving = jsnew(Rickshaw.Graph.Behavior.Series.Toggle)(l(graph = graph, legend = legend))
    val order = jsnew(Rickshaw.Graph.Behavior.Series.Order)(l(graph = graph, legend = legend))
    val highlighter = jsnew(Rickshaw.Graph.Behavior.Series.Highlight)(l(graph = graph, legend = legend))
    val hoverDetail = jsnew(Rickshaw.Graph.HoverDetail)(l(graph = graph, xFormatter = timestepDisplay))

    xAxis.render()
    yAxis.render()

    Obs(renderMode) {
      graph.setRenderer(renderMode())
      graph.render()
    }

    graph
  }
}
