package com.oomagnitude.api

import com.oomagnitude.metrics.model.MetricMetadata
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.util.Random

trait ExperimentApi {
  def experiments(): Future[List[String]]

  def dates(id: ExperimentId): Future[List[String]]

  def dataSources(id: ExperimentRunId): Future[List[MetricMetadata]]
}

class DummyExperimentApi(experimentList: List[String], dateList: List[String],
                         dataSourceList: List[MetricMetadata]) extends ExperimentApi {
  override def experiments(): Future[List[String]] =
    Future.successful(experimentList.filter(s => Random.nextBoolean()))

  override def dates(id: ExperimentId): Future[List[String]] =
    Future.successful(dateList.filter(s => Random.nextBoolean()))

  override def dataSources(id: ExperimentRunId): Future[List[MetricMetadata]] =
    Future.successful(dataSourceList.filter(s => Random.nextBoolean()))
}

object RemoteExperimentApi extends ExperimentApi {
  import Uris._

  import scala.concurrent.ExecutionContext.Implicits.global

  override def experiments(): Future[List[String]] =
    Ajax.get(experimentsUrl).map {
      xhr => upickle.read[List[String]](xhr.responseText)
    }

  override def dates(id: ExperimentId): Future[List[String]] =
    Ajax.get(datesUrl(id)).map {
      xhr => upickle.read[List[String]](xhr.responseText)
    }

  override def dataSources(id: ExperimentRunId): Future[List[MetricMetadata]] =
    Ajax.get(dataSourcesUrl(id)).map {
      xhr => upickle.read[List[MetricMetadata]](xhr.responseText)
    }
}
