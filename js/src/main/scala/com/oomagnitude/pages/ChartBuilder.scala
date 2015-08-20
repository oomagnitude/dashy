package com.oomagnitude.pages

import com.oomagnitude.bind.ViewChannel
import com.oomagnitude.css.Styles
import com.oomagnitude.metrics.model.Metrics._
import com.oomagnitude.model.{ChartData, ChartModel, ListWithId}
import com.oomagnitude.rx.Rxs
import com.oomagnitude.view._
import goggles.d3.scale.D3Scale
import org.scalajs.dom.html
import org.scalajs.dom.raw.MouseEvent

import scala.scalajs.js.annotation.JSExport
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa}


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
      val graph = forceGraph[MutualInfos, CellInfo, MutualInfo](data,
          aspectRatio = 2,
          linkDistance = { (width: Double, height: Double) => m: MutualInfo => (height / 20) + (1.0 - m.ejc) * (height / 10)},
          nodeStyle = { cells: Iterable[CellInfo] =>
            val colorScale = D3Scale.colorScale(cells.map(_.numConnections.toDouble), D3Scale.GreenGradient)

            {cell: CellInfo => List(Styles.graphNode, sa.fill := colorScale(cell.numConnections),
              title:=s"${cell.id},${cell.numConnections} connections")}
          },
          linkStyle = { mutualInfo: Iterable[MutualInfo] => mutualInfo: MutualInfo => List(Styles.graphLink)})(forceGraph.mutualInfoToGraph, forceGraph.mutualInfoLinks)
      removablePanel(data.params.title, SeekableChart(data, graph), {() => data.close(); remove()})
    }
    channel.bind(gaussiansData) {(data: ChartData[LabeledGaussians], remove: () => Unit) =>
      // TODO: extract params in a safe way
      val params = data.params.dataSources.head.parameters.get.asInstanceOf[GaussianParams]
      val element = Gabor(data, aspectRatio = 1, params)

      removablePanel(data.params.title, SeekableChart(data, element), {() => data.close(); remove()})
    }

    val builderData = new ChartModel

    // TODO: only allow data sources of the same "type" to be selected together
    val dataSourceForm = chartBuilderForm(builderData,
      {e: MouseEvent =>
        if (builderData.nonEmpty) {
          val first = builderData.selectedDataSources.items().head
          first.zero match {
            case _: MutualInfos =>
              forceGraphData.add(new ChartData(builderData.toParams, initiallyPaused = true))
            case Count(_) | Scalar(_)| Time(_, _) =>
              timeSeriesData.add(new ChartData(builderData.toParams))
            case LabeledGaussians(_, _, _) =>
              // TODO: support time lapse
              gaussiansData.add(new ChartData(builderData.toParams, initiallyPaused = true))
            case _ => // do nothing
          }
        }
      })

    container.appendChild(bs.well(h3("Chart Builder")).render)
    container.appendChild(bs.col12(dataSourceForm).render)
    container.appendChild(panels.elements.asFrags.render)
  }

}
