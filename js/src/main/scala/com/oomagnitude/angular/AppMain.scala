package com.oomagnitude.angular

import biz.enef.angulate._
import com.oomagnitude.angular.controllers.{ChartController, ExperimentController}
import com.oomagnitude.angular.service.{ExperimentService, DisplaySettingService}

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
    module.serviceOf[DisplaySettingService]

    module.run(initApp _)
  }

  def initApp(): Unit = {
//    angular.element(".combobox").combobox()
  }
}
