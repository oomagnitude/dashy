package com.oomagnitude.model

import com.oomagnitude.api.{AutowireClient, ExperimentApi}
import com.oomagnitude.metrics.model.{DataSourceId, ExperimentId, ExperimentRunId, MetricMetadata}
import com.oomagnitude.rx.Rxs._
import rx._
import autowire._

class ExperimentSelection {
  import scala.concurrent.ExecutionContext.Implicits.global
  private[this] val es = Var(List.empty[ExperimentId])
  private[this] val ds = Var(List.empty[ExperimentRunId])
  private[this] val dss = Var(List.empty[MetricMetadata])

  val dates: Rx[List[ExperimentRunId]] = ds
  val experiments: Rx[List[ExperimentId]] = es
  val dataSources: Rx[List[MetricMetadata]] = dss

  val experiment = Var("")
  val date = Var("")
  val dataSource = Var("")

  // clear date field whenever experiment field changes
  Obs(experiment){date() = ""}
  // clear data source field whenever date field changes
  Obs(date){dataSource() = ""}

  val experimentId = Rx {
    if (experiment().nonEmpty) Some(ExperimentId(experiment()))
    else None
  }

  val experimentRunId = conditional(experimentId, date,
  {(id: ExperimentId, str: String) => ExperimentRunId(id, str)})

  val dataSourceId = conditional(experimentRunId, dataSource,
    {(id: ExperimentRunId, str: String) => DataSourceId(id, str)})

  // fetch experiments immediately
  AutowireClient[ExperimentApi].experiments().call().onSuccess {case e => es() = e}
  // fetch dates when experiment ID is selected
  fetchOnChange(experimentId, ds, {id: ExperimentId => AutowireClient[ExperimentApi].experimentRuns(id).call()})
  // fetch data sources when experiment run ID is selected
  fetchOnChange(experimentRunId, dss, {id: ExperimentRunId => AutowireClient[ExperimentApi].dataSources(id).call()})

  def clear(): Unit = experiment() = ""
}
