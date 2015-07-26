package d3.examples

import com.oomagnitude.css._
import d3.examples.Data._
import org.scalajs.dom.html
import rx._
import svg._
import upickle.{default => upickle}
import viz.layout.{CoordinateAndExtent, LayoutNode}
import viz.shape.ArcDatum

import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.{js => sjs}
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}

/**
 * [[http://bl.ocks.org/mbostock/4348373]]
 */
@JSExport
object ZoomableSunburst {

  @JSExport
  def main(container: html.Div): Unit = {
    import com.oomagnitude.dom.all._
    import d3.all._

    val aspectRatio = 1.3714
    val (width, height) = viz.dimensions(aspectRatio)
    val radius = Math.min(width, height) / 2.0

    val x = d3.scale.linear[Double, Double].range(0, 2 * Math.PI)
    val y = d3.scale.squareRoot.range(0, radius)

    implicit val partitionNodeArc = new ArcDatum[LayoutNode[CoordinateAndExtent]] {
      override def outerRadius(d: LayoutNode[CoordinateAndExtent]): Double = Math.max(0, y(d.layout.y + d.layout.dy))
      override def innerRadius(d: LayoutNode[CoordinateAndExtent]): Double = Math.max(0, y(d.layout.y))
      override def startAngle(d: LayoutNode[CoordinateAndExtent]): Double = Math.max(0, Math.min(2 * Math.PI, x(d.layout.x)))
      override def endAngle(d: LayoutNode[CoordinateAndExtent]): Double = Math.max(0, Math.min(2 * Math.PI, x(d.layout.x + d.layout.dx)))
      override def padAngle(item: LayoutNode[CoordinateAndExtent]): Double = 0.0
    }

    val arc = d3.shape.arc
    val color = d3.scale.category20c[String]

    val treeData = upickle.read[TreeNode](treeJson)
    val layout = d3.layout.partition
      .disableSort()
      .value(1)
      .layout(treeData)

    val partitions = layout.nodes.map { n =>
      val pathArc = Var(arc(n))
      val colorOrdinal = if (n.isLeaf) n.structure.parentId.getOrElse("") else n.id
      val path = st.path(styles.partitionPath, sa.fill := color(colorOrdinal)).render.bind(sa.d, pathArc)

      (path, (n, pathArc))
    }

    // When zooming: interpolate the scales.
    partitions.onclick({kv: (LayoutNode[CoordinateAndExtent], Var[String]) =>
      val clicked = kv._1
      // Set the interpolation for the node that was clicked
      val xDomain = d3.interpolate(x.domain.toJSArray, sjs.Array(clicked.layout.x, clicked.layout.x + clicked.layout.dx))
      val yDomain = d3.interpolate(y.domain.toJSArray, sjs.Array(clicked.layout.y, 1.0))
      val yRange = d3.interpolate(y.range.toJSArray, sjs.Array(if (clicked.layout.y > 0) 20.0 else 0.0, radius))

      // Install a callback for each tick of the transition, so that each element is updated
      partitions.transition.duration(1000).tween("arc",
        { case ((node, arcPath), index) =>

          {t: Double =>
            if (index == 0) {
              x.domain(xDomain(t))
              y.domain(yDomain(t)).range(yRange(t))
            }
            arcPath() = arc(node)
          }
        })
    })

    container.appendChild(svgTag(aspectRatio)(styles.whiteBackground,
      st.g(sa.transform := transform.translate(width / 2.0, height / 2.0 + 10), partitions.map(_._1))).render)
  }

}
