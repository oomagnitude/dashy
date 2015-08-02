package com.oomagnitude.view

import com.oomagnitude.view.Templates._
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom
import scalatags.JsDom.all._

object RemovablePanel {
  def apply(panelTitle: Option[String], body: JsDom.Modifier, removed: () => Unit): HTMLElement = {
    val removeBtn =  div(cls:="btn-group pull-right", role:="group", removeButton(removed))


    val chartPanel =
    if (panelTitle.nonEmpty) {
      bs.panel(bs.panelHeading(h3(cls:="panel-title", panelTitle.get)), bs.panelBody(body))
    } else bs.panel(bs.panelBody(body))

    bs.row(bs.col12(removeBtn, chartPanel)).render
  }
}