package com.oomagnitude

import scala.scalajs.js.Dynamic.{global => g}

package object view {
  val DefaultHeight = 450
  // TODO: setting this to 100% results in availableWidth being NaN
  val DefaultWidth = 960

  val Rickshaw = g.Rickshaw

  val bs = Bootstrap

  val removablePanel = RemovablePanel

  val timeSeriesChart = TimeSeriesChart

  val charts = Charts

  val chartBuilderForm = ChartBuilderForm

  val forceGraph = ForceGraph

  val typeahead = Typeahead
}
