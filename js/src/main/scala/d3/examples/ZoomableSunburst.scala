package d3.examples

import com.oomagnitude.css._
import org.scalajs.dom.html
import rx._
import svg.{Svg, _}
import viz.layout.{PartitionNode, StufNThings}
import viz.shape.ArcDatum

import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JSON
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
    val (width, height) = Svg.dimensions(aspectRatio)
    val radius = Math.min(width, height) / 2.0

    val x = d3.scale.linear[Double, Double].range(0, 2 * Math.PI)
    val y = d3.scale.squareRoot.range(0, radius)
    val color = d3.scale.category20c[String]

    implicit val partitionNodeArc = new ArcDatum[PartitionNode] {
      override def outerRadius(d: PartitionNode): Double = Math.max(0, y(d.y + d.dy))
      override def innerRadius(d: PartitionNode): Double = Math.max(0, y(d.y))
      override def startAngle(d: PartitionNode): Double = Math.max(0, Math.min(2 * Math.PI, x(d.x)))
      override def endAngle(d: PartitionNode): Double = Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)))
      override def padAngle(item: PartitionNode): Double = 0.0
    }

    val partition = d3.layout.partition
      .disableSort()
      .value({ _: StufNThings => 1 })

    val arc = d3.shape.arc
    val treeData = new ConcreteTree(JSON.parse(Data.treeJson).asInstanceOf[TestTree])

    val partitions = partition.nodes(treeData).map { n =>
      val pathArc = Var(arc(n))
      val colorOrdinal = if (n.isLeaf) n.parentId.getOrElse("") else n.id
      val path = st.path(styles.partitionPath, sa.fill := color(colorOrdinal)).render
        .bind(sa.d, pathArc)

      (path, (n, pathArc))
    }

    // When zooming: interpolate the scales.
    partitions.onclick({kv: (PartitionNode, Var[String]) =>
      val clickedData = kv._1
      // Set the interpolation for the node that was clicked
      val xd = d3.interpolate(x.domain.toJSArray, sjs.Array(clickedData.x, clickedData.x + clickedData.dx))
      val yd = d3.interpolate(y.domain.toJSArray, sjs.Array(clickedData.y, 1.0))
      val yr = d3.interpolate(y.range.toJSArray, sjs.Array(if (clickedData.y > 0) 20.0 else 0.0, radius))

      // Install a callback for each tick of the transition, so that each element is updated
      partitions.zipWithIndex.foreach { case ((path, (node, pathArc)), index) =>
        path.transition(node).duration(1000).tween("arc", {t: Double =>
          if (index == 0) {
            x.domain(xd(t))
            y.domain(yd(t)).range(yr(t))
          }
          pathArc() = arc(node)
        })
      }
    })

    container.appendChild(svgTag(aspectRatio)(styles.whiteBackground,
      st.g(sa.transform := transform.translate(width / 2.0, height / 2.0 + 10), partitions.map(_._1))).render)
  }

}
