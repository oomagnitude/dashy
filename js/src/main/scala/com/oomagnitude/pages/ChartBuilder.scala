package com.oomagnitude.pages

import com.oomagnitude.bind.ViewChannel
import com.oomagnitude.metrics.model.Metrics._
import com.oomagnitude.model.{ChartData, ChartModel, ListWithId}
import com.oomagnitude.rx.Rxs
import com.oomagnitude.view._
import org.scalajs.dom.html
import org.scalajs.dom.raw.MouseEvent

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object ChartBuilder {
  import Rxs._

  @JSExport
  def main(container: html.Div): Unit = {
    val panels = new RxElementGroup()
    val channel = new ViewChannel(panels)
    val timeSeriesData = new ListWithId[ChartData[Double]]
    val forceGraphData = new ListWithId[ChartData[MutualInfos]]
    val gaussiansData = new ListWithId[ChartData[LabeledGaussians]]
    channel.bind(timeSeriesData) {(data: ChartData[Double], remove: () => Unit) =>
      removablePanel(data.params.title, timeSeriesChart(data), {() => data.close(); remove()})
    }
    channel.bind(forceGraphData) {(data: ChartData[MutualInfos], remove: () => Unit) =>
      removablePanel(data.params.title, md3.forceGraph(data)(md3.forceGraph.mutualInfoToGraph),
        {() => data.close(); remove()})
    }
    channel.bind(gaussiansData) {(data: ChartData[LabeledGaussians], remove: () => Unit) =>
      // TODO: extract params in a safe way
      val params = data.params.dataSources.head.parameters.get.asInstanceOf[GaussianParams]

      removablePanel(data.params.title, com.oomagnitude.view.Gabor(data, params), {() => data.close(); remove()})
    }

    val builderData = new ChartModel

    // TODO: only allow data sources of the same "type" to be selected together
    val dataSourceForm = chartBuilderForm(builderData,
      {e: MouseEvent =>
        if (builderData.nonEmpty) {
          val first = builderData.selectedDataSources.items().head
          first.zero match {
            case _: MutualInfos =>
              forceGraphData.add(new ChartData(builderData.toParams, initiallyPaused = true,
                {d: ChartData[MutualInfos] => d.location() = 90000; d.next()}))
            case Count(_) | Scalar(_)| Time(_, _) =>
              timeSeriesData.add(new ChartData(builderData.toParams))
            case LabeledGaussians(_) =>
              gaussiansData.add(new ChartData(builderData.toParams, initiallyPaused = true,
                {d: ChartData[LabeledGaussians] => d.location() = 90000; d.next()}))
            case _ => // do nothing
          }
        }
      })

    container.appendChild(bs.well(h3("Chart Builder")).render)
    container.appendChild(bs.col12(dataSourceForm).render)
    container.appendChild(panels.elements.asFrags.render)
  }

}
