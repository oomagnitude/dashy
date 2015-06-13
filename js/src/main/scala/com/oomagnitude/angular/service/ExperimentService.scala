package com.oomagnitude.angular.service

import biz.enef.angulate.Service
import biz.enef.angulate.core.{HttpPromise, HttpService}
import com.oomagnitude.Uris
import com.oomagnitude.api.{ExperimentRunId, ExperimentId, DataSourceId}
import ngwebsocket.{NgWebsocket, NgWebsocketService}

import scala.scalajs.js

class ExperimentService($http: HttpService, $websocket: NgWebsocketService, displaySettingService: DisplaySettingService) extends Service {
  val experimentSelection = js.Dynamic.literal(selectedExperiment = "", selectedDate = "", selectedDataSource = "")
  import Uris._

  def resetExperiment(): Unit = {
    experimentSelection.selectedExperiment = ""
    resetDate()
  }

  def resetDate(): Unit = {
    experimentSelection.selectedDate = ""
    resetDataSource()
  }

  def resetDataSource(): Unit = {
    experimentSelection.selectedDataSource = ""
  }

  // TODO: config for URL
  def experiments(): HttpPromise[js.Array[String]] = $http.get(experimentsUrl)

  def dates(experiment: String): HttpPromise[js.Array[String]] = $http.get(datesUrl(ExperimentId(experiment)))

  def dataSources(experiment: String, date: String): HttpPromise[js.Array[String]] =
    $http.get(dataSourcesUrl(ExperimentRunId(experiment, date)))

  def dataSource(experiment: String, date: String, dataSource: String): NgWebsocket = {
    val url = dataSourceUrl(DataSourceId(experiment, date, dataSource))
    $websocket.$new(url)
  }

}
