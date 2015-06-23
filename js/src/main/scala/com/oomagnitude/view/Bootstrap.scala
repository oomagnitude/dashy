package com.oomagnitude.view

import scalatags.JsDom.all._

object Bootstrap {
  def row = div(cls:="row")

  def col1 = div(cls:="col-lg-1")

  def col2 = div(cls:="col-lg-2")

  def col3 = div(cls:="col-lg-3")

  def col4 = div(cls:="col-lg-4")

  def col5 = div(cls:="col-lg-5")

  def col6 = div(cls:="col-lg-6")

  def col7 = div(cls:="col-lg-7")

  def col8 = div(cls:="col-lg-8")

  def col9 = div(cls:="col-lg-9")

  def col10 = div(cls:="col-lg-10")

  def col11 = div(cls:="col-lg-11")

  def col12 = div(cls:="col-lg-12")

  def well = div(cls:="well well-sm")

  def container = div(cls:="container")

  def panel = div(cls:="panel panel-default")

  def panelHeading = div(cls:="panel-heading")

  def panelBody = div(cls:="panel-body")

  def removeCircle = span(cls:="glyphicon glyphicon-remove-circle")

  def center = div(cls:="center-block")

  def formGroup = div(cls:="form-group")

  def formInline = div(cls:="form-inline")

  def formHorizontal = div(cls:="form-horizontal")

  def btnGroup = div(cls:="btn-group", role:="group")

  def btnPrimary = button(cls:="btn btn-primary", `type`:="button")

  def btnDefault = button(cls:="btn btn-default", `type`:="button")
}
