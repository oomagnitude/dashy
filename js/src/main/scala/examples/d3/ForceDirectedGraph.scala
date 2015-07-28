package examples.d3

import com.oomagnitude.css.{Styles => css}
import com.oomagnitude.dom.all._
import d3.all._
import org.scalajs.dom.html
import svg.Svg
import upickle.{default => upickle}

import scala.scalajs.js.annotation.JSExport
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}

/**
 * [[http://bl.ocks.org/mbostock/4062045]]
 */
@JSExport
object ForceDirectedGraph {
  case class GraphNode(name: String, group: Int)
  case class GraphLink(source: Int, target: Int, value: Int)
  case class Graph(nodes: IndexedSeq[GraphNode], links: IndexedSeq[GraphLink])

  @JSExport
  def main(container: html.Div): Unit = {
    val graph = upickle.read[Graph](Data.graphJson)

    val aspectRatio = 1.4
    val (width, height) = viz.dimensions(aspectRatio)

    val color = d3.scale.category20c[Int]
    val force = d3.layout.force
      .charge(-120)
      .linkDistance(30)
      .size(width, height)

    force.init(graph.nodes.size, graph.links.map(l => (l.source, l.target))).start()

    val lines = force.lines.zipWithIndex.map {
      case (line, index) =>
        val link = graph.links(index)
        st.line(css.graphLink, sa.strokeWidth:=math.sqrt(link.value)).render
          .bindOption(sa.x1, line.source.x)
          .bindOption(sa.y1, line.source.y)
          .bindOption(sa.x2, line.target.x)
          .bindOption(sa.y2, line.target.y)
    }

    val circles = force.points.zipWithIndex.map {
      case (point, index) =>
        val node = graph.nodes(index)
        st.circle(css.graphNode, sa.r:= 5, sa.fill:=color(node.group)).render
          .bindOption(sa.cx, point.x)
          .bindOption(sa.cy, point.y)
    }
    force.drag(circles)

    container.appendChild(Svg(aspectRatio)(css.whiteBackground, lines, circles).render)
  }
}
