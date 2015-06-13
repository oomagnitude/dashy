package com.oomagnitude.rx

import com.oomagnitude.api.DataSourceFetchParams
import com.oomagnitude.rx.api.RemoteExperimentApi
import com.oomagnitude.rx.model.{ChartData, DataSourceSelection, ExperimentSelection}
import com.oomagnitude.rx.view.Charts.Line
import com.oomagnitude.rx.view.{Chart, Templates}
import combobox.Combobox
import org.scalajs.dom.html
import org.scalajs.jquery._
import rx._

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object Client {
  import Rxs._
  import Templates._

  val DefaultFetchParams = DataSourceFetchParams(initialBatchSize = 100, frequencySeconds = Some(5),
    resolution = 10, timestepOffset = None)
  val fetchParams = Var(DefaultFetchParams)
  val charts = Var(List.empty[Chart])
  val chartContainers = Rx{charts().map(_.container)}

  @JSExport
  def main(container: html.Div): Unit = {

    val expSelection = new ExperimentSelection(RemoteExperimentApi)
    val dataSourceSelection = new DataSourceSelection(expSelection.dataSourceId)

    val dataSourceForm = experimentForm(expSelection, dataSourceSelection.selectedSources,
      {event =>
        val chartData = new ChartData(dataSourceSelection.selectedSources(), Var(DefaultFetchParams))
        charts() = new Chart(Line, chartData, {id => charts() = charts().filterNot(_.id == id)},
          title = Some("Title")) :: charts()
      })

    container.appendChild(
      bootstrap.container(
        bootstrap.well(h3("Chart Builder")),
        bootstrap.row(bootstrap.col12(dataSourceForm)),
        bootstrap.row(chartContainers.asFrags())
      ).render)

    Combobox.refresh(jQuery(".combobox"))
  }

}
