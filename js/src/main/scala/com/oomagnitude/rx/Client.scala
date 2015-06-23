package com.oomagnitude.rx

import com.oomagnitude.api.{DataSourceId, MutualInfos}
import com.oomagnitude.rx.api.RemoteExperimentApi
import com.oomagnitude.rx.model.{ChartData, ExperimentSelection}
import com.oomagnitude.view._
import jquery.JQueryExt
import org.scalajs.dom.html
import org.scalajs.jquery._
import rx._

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object Client {
  import Rxs._

  @JSExport
  def main(container: html.Div): Unit = {
    val title: Var[Option[String]] = Var(None)

    val timeSeriesPanels = new ElementGroup[ChartData[Double]]({
      (data, remove) =>
        removablePanel(title(), timeSeriesChart(data), {() => data.close(); remove()})
    })

    val forceGraphPanels = new ElementGroup[ChartData[MutualInfos]]({
      (data, remove) =>
        val element = div().render
        md3.forceGraph(element, data.signals.head._2) {
          dataPoint =>
            val mis = dataPoint.value
            val nodes = mis.cells.map(i => md3.forceGraph.Node(i.id, i.numConnections / 10))
            val nodeMap = nodes.zipWithIndex.map(kv => kv._1.name -> kv._2).toMap
            val links = mis.links.map(l => md3.forceGraph.Link(nodeMap(l.cells._1), nodeMap(l.cells._2), 150 - 100 * l.ejc))
            md3.forceGraph.Graph(nodes, links)
        }
        removablePanel(title(), element, {() => data.close(); remove()})
    })

    val expSelection = new ExperimentSelection(RemoteExperimentApi)
    val dataSourceTags = new RxOptionElementGroup[DataSourceId](expSelection.dataSourceId, {
      (dataSourceId, remove) => Templates.tag(dataSourceId.toString, {e => remove()})
    })

    val dataSourceForm = experimentForm(expSelection, dataSourceTags, title,
      {e =>
        if (dataSourceTags.items().size == 1 && dataSourceTags.items().head.name == "mutualInformation.json") {
          val data = new ChartData(dataSourceTags.items(), MutualInfos.zero, initiallyPaused = true,
            {d: ChartData[MutualInfos] => d.location() = 90000; d.next()})
          forceGraphPanels.add(data)
        } else timeSeriesPanels.add(new ChartData(dataSourceTags.items(), 0.0))})

    container.appendChild(
      bs.container(
        bs.well(h3("Chart Builder")),
        bs.row(bs.col12(dataSourceForm)),
        bs.row(timeSeriesPanels.elements.asFrags()),
        bs.row(forceGraphPanels.elements.asFrags())
      ).render)

    JQueryExt.refresh(jQuery(".combobox"))
  }

}
