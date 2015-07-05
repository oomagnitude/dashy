package com.oomagnitude.pages

import com.oomagnitude.api.{CustomType, Number}
import com.oomagnitude.bind.ViewChannel
import com.oomagnitude.metrics.model.ext.MutualInfos
import com.oomagnitude.model.{ChartData, ChartModel, ListWithId}
import com.oomagnitude.rx.Rxs
import com.oomagnitude.view._
import org.scalajs.dom.html

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
    channel.bind(timeSeriesData) {(data: ChartData[Double], remove: () => Unit) =>
      removablePanel(data.params.title, timeSeriesChart(data), {() => data.close(); remove()})
    }
    channel.bind(forceGraphData) {(data: ChartData[MutualInfos], remove: () => Unit) =>
      val element = div().render
      md3.forceGraph(element, data.signal) {
        dataPoints =>
          val mis = dataPoints.head._2.value
          val nodes = mis.cells.map(i => md3.forceGraph.Node(i.id, i.numConnections / 10))
          val nodeMap = nodes.zipWithIndex.map(kv => kv._1.name -> kv._2).toMap
          val links = mis.links.map(l => md3.forceGraph.Link(nodeMap(l.cells._1), nodeMap(l.cells._2), 150 - 100 * l.ejc))
          md3.forceGraph.Graph(nodes, links)
      }
      removablePanel(data.params.title, element, {() => data.close(); remove()})
    }

    val builderData = new ChartModel

    val dataSourceForm = chartBuilderForm(builderData,
      {e =>
        // TODO: remove manual check (use metadata instead)
        if (builderData.selectedDataSources.items().size == 1 && builderData.selectedDataSources.items().head.id.metricId.toString == "mutualInformation") {
          val data = new ChartData(builderData.toParams, CustomType, initiallyPaused = true,
            {d: ChartData[MutualInfos] => d.location() = 90000; d.next()})
          forceGraphData.add(data)
        } else timeSeriesData.add(new ChartData(builderData.toParams, Number))})

    container.appendChild(bs.well(h3("Chart Builder")).render)
    container.appendChild(bs.col12(dataSourceForm).render)
    container.appendChild(panels.elements.asFrags.render)
  }

}
