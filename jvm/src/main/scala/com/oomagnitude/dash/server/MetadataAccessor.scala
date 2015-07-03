package com.oomagnitude.dash.server

import com.oomagnitude.api.{DataSourceId, ExperimentRunId}
import com.oomagnitude.metrics.model.MetricMetadata

import scala.concurrent.Future

trait MetadataAccessor {
  def metadata(experimentRun: ExperimentRunId): Future[Seq[MetricMetadata]]

  def metadata(dataSources: Iterable[DataSourceId]): Future[Seq[(DataSourceId, MetricMetadata)]]
}


