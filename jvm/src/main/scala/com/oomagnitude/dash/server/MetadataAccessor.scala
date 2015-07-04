package com.oomagnitude.dash.server

import com.oomagnitude.metrics.model.{DataSourceId, ExperimentRunId, MetricMetadata}

import scala.concurrent.Future

trait MetadataAccessor {
  def metadata(experimentRun: ExperimentRunId): Future[Seq[MetricMetadata]]

  def metadata(dataSources: Iterable[DataSourceId]): Future[Seq[(DataSourceId, MetricMetadata)]]
}


