package com.oomagnitude.api

import com.oomagnitude.metrics.model.DataSourceId
import org.scalajs.dom

import scala.scalajs.js

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
    webSocketAddress + s"/ws/data?dataSources=$encodedDataSources&paused=$paused&dataType=$encodedDataType"
  }
}
