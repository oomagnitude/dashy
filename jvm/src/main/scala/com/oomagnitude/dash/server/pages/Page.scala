package com.oomagnitude.dash.server.pages

import com.oomagnitude.css.Styles

import scalacss.Defaults._
import scalacss.ScalatagsCss._
import scalatags.Text._
import scalatags.Text.all._

object Page {
  val ChartBuilderBoot = "com.oomagnitude.pages.ChartBuilder().main(document.getElementById('contents'))"
  val GaborBoot = "com.oomagnitude.pages.Gabor().main(document.getElementById('contents'))"

  def skeleton(boot: String) =
    html(
      head(
        meta(charset:="utf-8"),
        Styles.render[TypedTag[String]],
        link(rel:="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.css"),
        link(rel:="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.4/css/bootstrap.min.css"),
        link(rel:="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/rickshaw/1.5.1/rickshaw.min.css"),
        link(rel:="stylesheet", href:="css/graph.css")
      ),
      body(
        onload:=boot,
        div(id:="contents", cls:="container"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.4/js/bootstrap.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/typeahead.js/0.11.1/typeahead.bundle.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.5/d3.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/d3-tip/0.6.3/d3-tip.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/rickshaw/1.5.1/rickshaw.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.3/moment.min.js"),
        script(src:="js/scalajs-dashboard-fastopt.js")
      )
    )
}