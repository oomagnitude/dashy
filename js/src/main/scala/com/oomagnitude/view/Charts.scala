package com.oomagnitude.view

import com.oomagnitude.api.{DataPoint, DataSourceId}
import org.scalajs.dom.html
import rx._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l, newInstance => jsnew}

object Charts {
  val DefaultDataPointsPerSeries = 100

  def defaultRickshawChart(chartContainer: html.Element, legendContainer: html.Element, renderMode: Rx[String],
                           signals: Seq[(DataSourceId, Rx[DataPoint[Double]])], clearChart: Rx[_]): js.Dynamic = {
    val series = new js.Array[js.Dynamic]()
    val palette = jsnew(Rickshaw.Color.Palette)()
    var arrays = List.empty[(Rx[DataPoint[Double]], js.Array[js.Dynamic])]

    signals.foreach {
      case (id, signal) =>
        val array = new js.Array[js.Dynamic]()

        // TODO: observer for clear buffer
        series.push(l(name = id.toString, data = array, color = palette.color()))
        arrays = (signal, array) :: arrays
    }

    val graph = jsnew(Rickshaw.Graph)(l(element = chartContainer, renderer = "line", series = series,
      width = DefaultWidth, height = DefaultHeight))

    arrays.foreach {
      case (signal, array) =>
        Obs(signal) {
          array.push(l(x = signal().timestep, y = signal().value))
          if (array.length > DefaultDataPointsPerSeries) { array.shift() }
          graph.update()
        }
    }

    Obs(clearChart) {
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
