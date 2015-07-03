package com.oomagnitude.api

import scalajs.js
import org.scalajs.dom

object Uris {
  def webSocketAddress: String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}"
  }

  // TODO: find suitable URI construction library with safe encoding
  // TODO: make URI encoding work with both js and jvm
  def dataSourceUrl(ids: List[DataSourceId], paused: Boolean, dataType: MetricDataType): String = {
    val encodedDataSources = js.Dynamic.global.encodeURIComponent(upickle.write(ids)).asInstanceOf[String]
    val encodedDataType = js.Dynamic.global.encodeURIComponent(upickle.write(dataType)).asInstanceOf[String]
    webSocketAddress + s"/api/data?dataSources=$encodedDataSources&paused=$paused&dataType=$encodedDataType"
  }

  val experimentsUrl = "/api/experiments"

  def datesUrl(id: ExperimentId) = s"$experimentsUrl/${id.experiment}"

  def dataSourcesUrl(id: ExperimentRunId) = s"$experimentsUrl/${id.experiment}/${id.date}"
}
