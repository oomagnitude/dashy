package com.oomagnitude.js.controllers

import biz.enef.angulate.ScopeController
import com.oomagnitude.js.service.{DisplaySettingService, ExperimentService}
import ngwebsocket.NgWebsocket

import scala.scalajs.js
import scala.scalajs.js.JSON

// TODO: pass in chart options from the outside
class ChartController($scope: js.Dynamic, experimentService: ExperimentService, displaySettingService: DisplaySettingService) extends ScopeController {
  import com.oomagnitude.js.JsImplicits._

  var webSocket: Option[NgWebsocket] = None

  $scope.experimentSelection = experimentService.experimentSelection

  // Params:
  // data point interval (in seconds)
  // data point resolution (how many timesteps to skip per data point)
  // starting timestep

  val margin = js.Dynamic.literal(top = 20, right = 20, bottom = 40, left = 55)
  val chart = js.Dynamic.literal(`type` = "lineChart", height = 450, width=960, margin = margin,
    useInteractiveGuideline = true, transitionDuration = 500, x = { d: js.Dynamic => d.timestep }, 
    y = { d: js.Dynamic => d.value })
  $scope.options = js.Dynamic.literal(chart = chart)

  $scope.data = js.Array[js.Object](js.Dynamic.literal(key = "series", values = js.Array[js.Object]()))

  $scope.$watch({() => $scope.experimentSelection.selectedDataSource}, {
    dataSource: String =>
      resetChart()
      if (dataSource.nonEmpty) {
        val experiment = $scope.experimentSelection.selectedExperiment.asInstanceOf[String]
        val date = $scope.experimentSelection.selectedDate.asInstanceOf[String]
        val ws = experimentService.dataSource(experiment, date, dataSource)
        ws.$on("$open", { () => println(s"opened connection") })

        ws.$on("$message", { (data: String) ⇒
          val parsed = JSON.parse(data).asInstanceOf[js.Object]
          $scope.$apply({ () => addDataPoint(parsed) })
        })

        ws.$on("$close", { () ⇒ println(s"closed connection") })

        // TODO: figure out type for error
        ws.$on("$error", { () ⇒ println(s"error") })

        webSocket = Some(ws)
      }
  })

  private def resetChart(): Unit = {
    webSocket.foreach(_.$close())
    webSocket = None
    while (numDataPoints() > 0) {
      // Empty the array without creating a new one, so that angular can observe the change
      $scope.data.asArray(0).values.shift()
    }
  }

  private def addDataPoint(dataPoint: js.Object): Unit = {
    $scope.data.asArray(0).values.push(dataPoint)
    if (numDataPoints() > displaySettingService.displaySettings.maxDataPointsPerSeries.asInstanceOf[Int]) {
      $scope.data.asArray(0).values.shift()
    }
  }

  private def numDataPoints(): Int = $scope.data.asArray(0).values.length.asInstanceOf[Int]

}
