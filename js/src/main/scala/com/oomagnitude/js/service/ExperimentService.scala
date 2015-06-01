package com.oomagnitude.js.service

import biz.enef.angulate.Service
import biz.enef.angulate.core.{HttpPromise, HttpService}
import scala.scalajs.js

class ExperimentService($http: HttpService) extends Service {
  private val baseUrl = "http://localhost:8000"
  // TODO: config for URL
  def experiments(): HttpPromise[js.Array[String]] = $http.get(s"$baseUrl/api/experiments")

  def dates(experiment: String): HttpPromise[js.Array[String]] = $http.get(s"$baseUrl/api/experiments/$experiment")
}

