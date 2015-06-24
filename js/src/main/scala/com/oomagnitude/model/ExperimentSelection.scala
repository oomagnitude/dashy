package com.oomagnitude.model

import com.oomagnitude.api.{DataSourceId, ExperimentId, ExperimentRunId}
import com.oomagnitude.rx.Rxs._
import com.oomagnitude.rx.api.ExperimentApi
import rx._

class ExperimentSelection(api: ExperimentApi) {
  import scala.concurrent.ExecutionContext.Implicits.global
  private[this] val es = Var(List.empty[String])
  private[this] val ds = Var(List.empty[String])
  private[this] val dss = Var(List.empty[String])

  val dates: Rx[List[String]] = ds
  val experiments: Rx[List[String]] = es
  val dataSources: Rx[List[String]] = dss

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
  {(id: ExperimentId, str: String) => ExperimentRunId(id.experiment, str)})

  val dataSourceId = conditional(experimentRunId, dataSource,
    {(id: ExperimentRunId, str: String) => DataSourceId(id.experiment, id.date, str)})

  // fetch experiments immediately
  api.experiments().onSuccess {case e => es() = e}
  // fetch dates when experiment ID is selected
  fetchOnChange(experimentId, ds, api.dates)
  // fetch data sources when experiment run ID is selected
  fetchOnChange(experimentRunId, dss, api.dataSources)

  def clear(): Unit = experiment() = ""
}
