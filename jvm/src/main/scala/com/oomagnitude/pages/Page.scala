package com.oomagnitude.pages

import scalatags.Text.all._

object Page {
  val Boot = "com.oomagnitude.rx.Client().main(document.getElementById('contents'))"

  val Skeleton =
    html(
      head(
        meta(charset:="utf-8"),
        link(rel:="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.4/css/bootstrap.min.css"),
        link(rel:="stylesheet", href:="https://cdnjs.cloudflare.com/ajax/libs/nvd3/1.7.1/nv.d3.min.css"),
        link(rel:="stylesheet", href:="css/bootstrap-combobox.css")
      ),
      body(
        onload:=Boot,
        div(id:="contents", cls:="container"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.4/js/bootstrap.min.js"),
        script(src:="js/bootstrap-combobox.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.5/d3.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/nvd3/1.7.1/nv.d3.js"),
        script(src:="js/scalajs-dashboard-fastopt.js"),
        script(src:="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.3/moment.min.js")
      )
    )
}