package com.oomagnitude.api

import com.oomagnitude.metrics.model.{DataSourceId, MetricMetadata, ExperimentRunId, ExperimentId}

import scala.concurrent.Future
import scala.util.Random

trait ExperimentApi {
  def experiments(): Future[List[ExperimentId]]

  def experimentRuns(id: ExperimentId): Future[List[ExperimentRunId]]

  def dataSources(id: ExperimentRunId): Future[List[MetricMetadata]]
  
  def metadata(dataSources: List[DataSourceId]): Future[List[MetricMetadata]]
}

class DummyExperimentApi(experimentList: List[ExperimentId], dateList: List[ExperimentRunId],
                         dataSourceList: List[MetricMetadata]) extends ExperimentApi {
  override def experiments(): Future[List[ExperimentId]] =
    Future.successful(experimentList.filter(s => Random.nextBoolean()))

  override def experimentRuns(id: ExperimentId): Future[List[ExperimentRunId]] =
    Future.successful(dateList.filter(s => Random.nextBoolean()))

  override def dataSources(id: ExperimentRunId): Future[List[MetricMetadata]] =
    Future.successful(dataSourceList.filter(s => Random.nextBoolean()))

  override def metadata(dataSources: List[DataSourceId]): Future[List[MetricMetadata]] = 
    Future.successful(dataSourceList.filter(ds =>dataSources.contains(ds.id)))
}