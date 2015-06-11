package com.oomagnitude.rx

import com.oomagnitude.api.DataSourceFetchParams
import com.oomagnitude.rx.api.{Series, RemoteDataSourceApi, RemoteExperimentApi}
import com.oomagnitude.rx.model.{DataSourceSelection, ExperimentSelection}
import com.oomagnitude.rx.view.{Charts, Templates}
import combobox.Combobox
import org.scalajs.dom.html
import org.scalajs.jquery._
import rx._

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object Client {
  import Charts._
  import Templates._

  val DefaultFetchParams = DataSourceFetchParams(initialBatchSize = 100, frequencySeconds = Some(5),
    resolution = Some(10))
  val fetchParams = Var(DefaultFetchParams)

  @JSExport
  def main(container: html.Div): Unit = {

    val expSelection = ExperimentSelection(RemoteExperimentApi)
    val dataSourceSelection = DataSourceSelection(RemoteDataSourceApi, expSelection.experimentRunId, fetchParams)
    val dataSourceForm = experimentForm(expSelection, dataSourceSelection.dataSource)
    val series = Rx{List(Series(dataSourceSelection.dataSource(), dataSourceSelection.dataPoints()))}

    val chartContainer = div(cls:="panel panel-default").render.addChart(defaultLineChart, series)

    container.appendChild(
      div(cls:="container",
        div(cls:="well", h3("Choose a data source")),
        div(cls:="row", dataSourceForm, chartContainer)
      ).render)

    Combobox.refresh(jQuery(".combobox"))
  }
}
