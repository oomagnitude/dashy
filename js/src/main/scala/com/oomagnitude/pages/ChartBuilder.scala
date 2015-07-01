package com.oomagnitude.pages

import com.oomagnitude.api.RemoteExperimentApi
import com.oomagnitude.bind.ViewChannel
import com.oomagnitude.metrics.model.ext.MutualInfos
import com.oomagnitude.model.{ChartBuilderData, ChartData, ListWithId}
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
      removablePanel(data.title, timeSeriesChart(data), {() => data.close(); remove()})
    }
    channel.bind(forceGraphData) {(data: ChartData[MutualInfos], remove: () => Unit) =>
      val element = div().render
      md3.forceGraph(element, data.signals.head._2) {
        dataPoint =>
          val mis = dataPoint.value
          val nodes = mis.cells.map(i => md3.forceGraph.Node(i.id, i.numConnections / 10))
          val nodeMap = nodes.zipWithIndex.map(kv => kv._1.name -> kv._2).toMap
          val links = mis.links.map(l => md3.forceGraph.Link(nodeMap(l.cells._1), nodeMap(l.cells._2), 150 - 100 * l.ejc))
          md3.forceGraph.Graph(nodes, links)
      }
      removablePanel(data.title, element, {() => data.close(); remove()})
    }

    val builderData = new ChartBuilderData(RemoteExperimentApi)

    val dataSourceForm = chartBuilderForm(builderData,
      {e =>
        if (builderData.dataSources.items().size == 1 && builderData.dataSources.items().head.name == "mutualInformation") {
          val data = new ChartData(builderData.title(), builderData.dataSources.items(), MutualInfos.zero, initiallyPaused = true,
            {d: ChartData[MutualInfos] => d.location() = 90000; d.next()})
          forceGraphData.add(data)
        } else timeSeriesData.add(new ChartData(builderData.title(), builderData.dataSources.items(), 0.0))})

    container.appendChild(bs.well(h3("Chart Builder")).render)
    container.appendChild(bs.col12(dataSourceForm).render)
    container.appendChild(panels.elements.asFrags.render)
  }

}
