package com.oomagnitude.js.service

import biz.enef.angulate.Service
import biz.enef.angulate.core.{HttpPromise, HttpService}
import ngwebsocket.{NgWebsocket, NgWebsocketService}
import org.scalajs.dom
import org.scalajs.dom.raw.Document

import scala.scalajs.js

class ExperimentService($http: HttpService, $websocket: NgWebsocketService) extends Service {
  val experimentSelection = js.Dynamic.literal(selectedExperiment = "", selectedDate = "", selectedDataSource = "")

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
  def experiments(): HttpPromise[js.Array[String]] = $http.get(s"/api/experiments")

  def dates(experiment: String): HttpPromise[js.Array[String]] = $http.get(s"/api/experiments/$experiment")

  def dataSources(experiment: String, date: String): HttpPromise[js.Array[String]] = $http.get(s"/api/experiments/$experiment/$date")

  def dataSource(experiment: String, date: String, dataSource: String): NgWebsocket = {
    $websocket.$new(s"${getWebsocketUri(dom.document)}/api/experiments/$experiment/$date/$dataSource")
  }

  def getWebsocketUri(document: Document): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"

    s"$wsProtocol://${dom.document.location.host}"
  }


}
