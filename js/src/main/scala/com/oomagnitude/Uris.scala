package com.oomagnitude

import com.oomagnitude.api.{DataSourceId, ExperimentId, ExperimentRunId}
import org.scalajs.dom

object Uris {
  def webSocketAddress: String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}"
  }

  // TODO: find suitable URI construction library with safe encoding
  def dataSourcePath(id: DataSourceId) = s"/api/data/${id.experiment}/${id.date}/${id.name}"

  def dataSourceUrl(id: DataSourceId, paused: Boolean) = {
    webSocketAddress + dataSourcePath(id) + s"?paused=$paused"
  }

  val experimentsUrl = "/api/experiments"

  def datesUrl(id: ExperimentId) = s"$experimentsUrl/${id.experiment}"

  def dataSourcesUrl(id: ExperimentRunId) = s"$experimentsUrl/${id.experiment}/${id.date}"
}
