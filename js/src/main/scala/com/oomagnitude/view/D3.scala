package com.oomagnitude.view

import com.oomagnitude.api.DataPoints
import com.oomagnitude.metrics.model.Metrics.MutualInfos
import com.oomagnitude.model.ChartData
import d3.D3Scale
import org.scalajs.dom.html
import rx._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scalatags.JsDom.all._

object D3 {
  private val d3 = js.Dynamic.global.d3

  object forceGraph {
    case class Node(name: String, group: Int) {
      def toJs = js.Dynamic.literal(name = name, group = group)
    }

    case class Link(source: Int, target: Int, value: Double) {
      def toJs = js.Dynamic.literal(source = source, target = target, value = value)
    }

    case class Graph(nodes: List[Node], links: List[Link])

    def mutualInfoToGraph(dataPoints: DataPoints[MutualInfos]): Graph = {
      val mis = dataPoints.head._2.value
      val nodes = mis.cells.map(i => Node(i.id, i.numConnections / 10))
      val nodeMap = nodes.zipWithIndex.map(kv => kv._1.name -> kv._2).toMap
      val links = mis.links.map(l => Link(nodeMap(l.cells._1), nodeMap(l.cells._2), 150 - 100 * l.ejc))
      Graph(nodes, links)
    }

    def apply[T](data: ChartData[T], aspectRatio: Double)(implicit convert: DataPoints[T] => Graph): html.Element = {
      implicit val nodeOrdering = new Ordering[Node] {
        override def compare(x: Node, y: Node): Int = x.group.compare(y.group)
      }
      val width = DefaultWidth
      val height = DefaultHeight

      val force = d3.layout.force()
        .charge(-120)
        .linkDistance({ d: js.Dynamic => d.value })
        .size(js.Array(width, height))

      val container = div().render

      // TODO: instead use: Svg(aspectRatio)(styles.greyBackground)
      val svg = d3.select(container).append("svg")
        .attr("width", width)
        .attr("height", height)
        .style("background-color", "#d8d8d8")

      val tip = d3.tip()
        .attr("class", "d3-tip")
        .offset(js.Array(-10, 0))
        .html({ d: js.Dynamic =>
          bs.row(bs.col10(strong(d.name.toString)), bs.col2(d.group.toString)).toString()})

      svg.call(tip)

      Obs(data.signal, skipInitial = true) {
        val graph: Graph = data.signal()
        val colorGradient = D3Scale.colorScale(graph.nodes.map(_.group.toDouble), D3Scale.GreenGradient)
        val nodes = graph.nodes.toJSArray.map(_.toJs)
        val links = graph.links.toJSArray.map(_.toJs)

        force.nodes(nodes).links(links).start()

        val link = svg.selectAll(".link")
          .data(links)
          .enter().append("line")
          .attr("class", "link")

        val node = svg.selectAll(".node")
          .data(nodes)
          .enter().append("circle")
          .attr("class", "node")
          .attr("r", 5)
          .style("fill", { d: js.Dynamic => colorGradient(d.group.asInstanceOf[Double]) })
          .on("mouseover", tip.show)
          .on("mouseout", tip.hide)
          .call(force.drag)

        force.on("tick", { () =>
          link.attr("x1", { d: js.Dynamic => d.source.x })
            .attr("y1", { d: js.Dynamic => d.source.y })
            .attr("x2", { d: js.Dynamic => d.target.x })
            .attr("y2", { d: js.Dynamic => d.target.y })

          node.attr("cx", { d: js.Dynamic => d.x })
            .attr("cy", { d: js.Dynamic => d.y })
        })
      }
      container
    }
  }
}
