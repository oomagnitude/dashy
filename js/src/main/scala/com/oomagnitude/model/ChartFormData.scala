package com.oomagnitude.model

import com.oomagnitude.api.{AutowireClient, ExperimentApi}
import com.oomagnitude.metrics.model.{MetricMetadata, ExperimentRunId, ExperimentId}
import com.oomagnitude.rx.Rxs._
import com.oomagnitude.view.SelectOption._
import rx._
import autowire._

trait ChartFormData {
  def experimentOptions: SelectOptions[ExperimentId]
  def experimentRunOptions: SelectOptions[ExperimentRunId]
  def dataSourceOptions: SelectOptions[MetricMetadata]
  def title: Var[Option[String]]
  def selectedDataSources: RxOptionList[MetricMetadata]
  def clear(): Unit
  def isEmpty: Boolean = selectedDataSources.items().isEmpty
  def nonEmpty: Boolean = !isEmpty
  def toParams = ChartParams(title(), selectedDataSources.items())
}

case class ChartParams(title: Option[String], dataSources: List[MetricMetadata])

class ChartModel extends ChartFormData {
  import scala.concurrent.ExecutionContext.Implicits.global
  private[this] val experimentIds = Var(List.empty[ExperimentId])
  // fetch experiments immediately
  AutowireClient[ExperimentApi].experiments().call().onSuccess{case e => experimentIds() = e}
  private[this] val experimentRunIds = Var(List.empty[ExperimentRunId])
  private[this] val metricMetadata = Var(List.empty[MetricMetadata])

  override val experimentOptions = new SelectOptions[ExperimentId](experimentIds, {_.toString},
    {id => id.experiment})
  override val experimentRunOptions = new SelectOptions[ExperimentRunId](experimentRunIds, {_.toString},
    {id => s"${moment.humanReadableDurationFromNow(id.date)} (${moment.calendarDate(id.date)})"})(idOrdering.reverse)
  override val dataSourceOptions = new SelectOptions[MetricMetadata](metricMetadata, {_.id.toString},
    {_.id.metricId.toString})

  // fetch dates when experiment ID is selected
  fetchOnChange(experimentOptions.selectedItem, experimentRunIds,
    {id: ExperimentId => AutowireClient[ExperimentApi].experimentRuns(id).call()})
  // fetch data sources when experiment run ID is selected
  fetchOnChange(experimentRunOptions.selectedItem, metricMetadata,
    {id: ExperimentRunId => AutowireClient[ExperimentApi].dataSources(id).call()})

  override val title: Var[Option[String]] = Var(None)
  override val selectedDataSources = new RxOptionList(dataSourceOptions.selectedItem)

  override def clear(): Unit = {
    selectedDataSources.clear()
    experimentOptions.clear()
    title() = None
  }
}
