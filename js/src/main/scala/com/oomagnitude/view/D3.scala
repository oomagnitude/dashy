package com.oomagnitude.view

import com.oomagnitude.api.DataPoints
import com.oomagnitude.collection.CollectionExt._
import com.oomagnitude.metrics.model.Metrics.MutualInfos
import com.oomagnitude.model.ChartData
import org.scalajs.dom.html
import rx._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JSON
import scalatags.JsDom.all._


object D3 {
  private val d3 = js.Dynamic.global.d3

  val GreenGradient = IndexedSeq("#f7fcfd", "#e5f5f9","#ccece6", "#99d8c9", "#66c2a4", "#41ae76", "#238b45", "#006d2c",
    "#00441b")
  val RedGradient = IndexedSeq("#f7fcfd","#e5f5f9","#ccece6", "#99d8c9", "#66c2a4", "#41ae76", "#238b45", "#006d2c",
    "#00441b")

  private def colorScale[T: Ordering](data: Iterable[T], colors: Seq[String])(toNumber: T => Double): js.Dynamic = {
    require(colors.size > 1, s"color scale must provide at least 2 colors for ${JSON.stringify(colors)}")

    val (min, max) = data.minAndMax
    val extent = toNumber(max) - toNumber(min)
    val increment = extent / (colors.size - 1)
    val domain = colors.indices.map(toNumber(min) + increment * _)

    d3.scale.linear()
      .domain(domain.toJSArray)
      .range(colors.toJSArray)
  }

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

    def apply[T](data: ChartData[T])(implicit convert: DataPoints[T] => Graph): html.Element = {
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
      val svg = d3.select(container).append("com/oomagnitude/svg")
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
        val colorGradient = colorScale(graph.nodes, GreenGradient){(d: Node) => d.group}
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
          .style("fill", { d: js.Dynamic => colorGradient(d.group) })
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
