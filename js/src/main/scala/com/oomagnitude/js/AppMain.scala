package com.oomagnitude.js

import biz.enef.angulate._
import com.oomagnitude.js.controllers.{ChartController, ExperimentController}
import com.oomagnitude.js.service.ExperimentService

import scala.language.implicitConversions
import scala.scalajs.js.JSApp


object AppMain extends JSApp {
  def main(): Unit = {
    setup()
  }

  def setup(): Unit = {
    val module = angular.createModule("app", "ngWebsocket" :: "nvd3" :: Nil)

    module.controllerOf[ExperimentController]("ExperimentController")
    module.controllerOf[ChartController]("ChartController")

    module.serviceOf[ExperimentService]

    module.run(initApp _)
  }

  def initApp(): Unit = {
//    angular.element(".combobox").combobox()
  }
}
