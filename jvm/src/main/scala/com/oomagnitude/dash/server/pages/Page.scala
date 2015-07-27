package com.oomagnitude.dash.server.pages

import com.oomagnitude.css.Styles

import scalacss.Defaults._
import scalacss.ScalatagsCss._
import scalatags.Text._
import scalatags.Text.all._

object Page {
  val ChartBuilderBoot = "com.oomagnitude.pages.ChartBuilder().main(document.getElementById('contents'))"
  val TreeBoot = "examples.d3.ReingoldTilfordTree().main(document.getElementById('contents'))"
  val RadialTreeBoot = "examples.d3.RadialReingoldTilfordTree().main(document.getElementById('contents'))"
  val ZoomablePartitionSunburstBoot = "examples.d3.ZoomableSunburst().main(document.getElementById('contents'))"
  val ForceGraphBoot = "examples.d3.ForceDirectedGraph().main(document.getElementById('contents'))"
  val ForceGraphDynamicBoot = "examples.d3.ForceDirectedGraphDynamic().main(document.getElementById('contents'))"
  val ForceGraphFacadeBoot = "examples.d3.ForceDirectedGraphFacade().main(document.getElementById('contents'))"
  val TimerBoot = "examples.rx.Timer().main(document.getElementById('contents'))"
  val TimerAngularBoot = "examples.rx.TimerAngular().main(document.getElementById('contents'))"

  val vizPages = Map(
    "tree" -> TreeBoot,
    "radialTree" -> RadialTreeBoot,
    "sunburst" -> ZoomablePartitionSunburstBoot,
    "forceGraphRx" -> ForceGraphBoot,
    "forceGraphDynamic" -> ForceGraphDynamicBoot,
    "forceGraphFacade" -> ForceGraphFacadeBoot,
    "timerRx" -> TimerBoot
  )

  def skeleton(boot: String) =
    html(
      head(
        meta(charset:="utf-8", name:="viewport", content:="width=device-width, initial-scale=1"),
        Styles.render[TypedTag[String]],
        link(rel:="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.css"),
        link(rel:="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.4/css/bootstrap.min.css"),
        link(rel:="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/rickshaw/1.5.1/rickshaw.min.css"),
        link(rel:="stylesheet", href:="/css/graph.css")
      ),
      body(
        onload:=boot,
        div(id:="contents", cls:="container"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.4/js/bootstrap.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/typeahead.js/0.11.1/typeahead.bundle.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.5/d3.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/rickshaw/1.5.1/rickshaw.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.3/moment.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.4.3/angular.min.js"),
        script(src:="/js/scalajs-dashboard-fastopt.js")
      )
    )
}