package com.oomagnitude.js

import biz.enef.angulate._
import com.oomagnitude.js.controllers.ExperimentController
import com.oomagnitude.js.service.ExperimentService

import scala.language.implicitConversions
import scala.scalajs.js.JSApp


object AppMain extends JSApp {
  def main(): Unit = {
    setup()
  }

  def setup(): Unit = {
    val module = angular.createModule("app", Nil)

    module.controllerOf[ExperimentController]("ExperimentController")

    module.serviceOf[ExperimentService]

    module.run(initApp _)
  }

  def initApp(): Unit = {
//    angular.element(".combobox").combobox()
  }
}
