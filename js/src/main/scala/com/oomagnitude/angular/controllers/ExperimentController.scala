package com.oomagnitude.angular.controllers

import biz.enef.angulate.ScopeController
import com.oomagnitude.angular.service.ExperimentService

import scala.scalajs.js
import scala.util.{Failure, Success}

class ExperimentController($scope: js.Dynamic, experimentService: ExperimentService) extends ScopeController {
  $scope.experiments = js.Array[String]()
  $scope.dates = js.Array[String]()
  $scope.dataSources = js.Array[String]()
  $scope.experimentSelection = experimentService.experimentSelection

  $scope.$watch({() => $scope.experimentSelection.selectedExperiment}, {
    experiment: String =>
      experimentService.resetDate()
      if (experiment.nonEmpty) {
        experimentService.dates(experiment) onComplete {
          case Success(res) =>
            $scope.dates = res
          case Failure(ex) => handleError(ex)
        }
      }
  })

  $scope.$watch({() => $scope.experimentSelection.selectedDate}, {
    date: String =>
      experimentService.resetDataSource()
      if (date.nonEmpty) {
        val experiment: String = $scope.experimentSelection.selectedExperiment.asInstanceOf[String]
        experimentService.dataSources(experiment, date) onComplete {
          case Success(res) =>
            $scope.dataSources = res
          case Failure(ex) => handleError(ex)
        }
      }
  })

  // TODO: get notified of new experiment runs w/o refreshing the page
  experimentService.experiments() onComplete {
    case Success(res) =>
      $scope.experiments = res
    case Failure(ex) => handleError(ex)
  }

  private def handleError(ex: Throwable): Unit = js.Dynamic.global.console.error(s"An error has occurred: $ex")
}

