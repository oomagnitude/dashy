package com.oomagnitude

import com.oomagnitude.api.{ExperimentRunId, ExperimentId, DataSourceFetchParams, DataSourceId}
import org.scalajs.dom

object Uris {
  def webSocketAddress: String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"

    s"$wsProtocol://${dom.document.location.host}"
  }

  def dataSourcePath(id: DataSourceId, params: DataSourceFetchParams): String = {
    // TODO: find suitable URI construction library with safe encoding
    s"/api/data/${id.experiment}/${id.date}/${id.name}?" +
      s"initialBatchSize=${params.initialBatchSize}" +
      (if (params.frequencySeconds.nonEmpty) s"&dataPointFrequencySeconds=${params.frequencySeconds.get}" else "") +
      (if (params.resolution.nonEmpty) s"&timestepResolution=${params.resolution.get}" else "")
  }

  def dataSourceUrl(id: DataSourceId, params: DataSourceFetchParams) : String = {
    webSocketAddress + dataSourcePath(id, params)
  }

  val experimentsUrl = "/api/experiments"

  def datesUrl(id: ExperimentId) = s"$experimentsUrl/${id.experiment}"

  def dataSourcesUrl(id: ExperimentRunId) = s"$experimentsUrl/${id.experiment}/${id.date}"
}
