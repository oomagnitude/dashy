package com.oomagnitude.js.controllers

import biz.enef.angulate.core.Location
import biz.enef.angulate.{ScopeController, Controller, Scope}
import com.oomagnitude.js.service.ExperimentService

import scala.scalajs.js
import scala.util.{Failure, Success}

class ExperimentController($scope: js.Dynamic, experimentService: ExperimentService, $location: Location) extends ScopeController {
//  val $dynamicScope = $scope.asInstanceOf[js.Dynamic]
  $scope.experiments = js.Array[String]()
  $scope.selectedExperiment = ""
  $scope.dates = js.Array[String]()
  $scope.selectedDate = ""

  $scope.$watch({() => $scope.selectedExperiment}, {
    s: String =>
      experimentService.dates(s) onComplete {
        case Success(res) =>
          $scope.dates = res
        case Failure(ex) => handleError(ex)
      }
      println($scope.selectedExperiment)
  })

  experimentService.experiments() onComplete {
    case Success(res) =>
      $scope.experiments = res
      println($scope.experiments)
    case Failure(ex) => handleError(ex)
  }

  private def handleError(ex: Throwable): Unit = js.Dynamic.global.console.error(s"An error has occurred: $ex")


}

