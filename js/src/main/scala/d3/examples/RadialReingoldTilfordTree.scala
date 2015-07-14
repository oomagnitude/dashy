package d3.examples

import com.oomagnitude.css._
import d3.all._
import org.scalajs.dom.html
import svg._

import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}
import viz.layout.TreeLayout._

@JSExport
object RadialReingoldTilfordTree {

  @JSExport
  def main(container: html.Div): Unit = {
    val aspectRatio = 1.185
    val (width, height) = Svg.dimensions(aspectRatio)
    val diameter = width
    val radius = diameter / 2.0

    val tree = d3.layout.tree
      .size(360, radius - 120)
      .separation({(a: ParentIdAndDepth, b: ParentIdAndDepth) => (if(a._1 == b._1) 1.0 else 2.0) / a._2.toDouble})

    val diagonal = d3.shape.diagonalRadial
      .projection({(x: Double, y: Double)=> (y, x / 180 * Math.PI) })

    val treeData = new ConcreteTree(JSON.parse(Data.treeJson).asInstanceOf[TestTree])

    val linkTags = tree.links(treeData).map {link => st.path(styles.treeLink, sa.d:=diagonal(link))}

    val nodeTags = tree.nodes(treeData).map { node =>
      val (anchor, trans) =
        if(node.x < 180) ("start", transform.translate(8))
        else ("end", transform.transforms(transform.rotate(180), transform.translate(-8)))
      st.g(styles.treeNode, sa.transform:=transform.transforms(transform.rotate(node.x - 90), transform.translate(node.y)),
        st.circle(styles.treeNodeCircle, sa.r:= 2.5),
        st.text(sa.dy:=".31em", sa.textAnchor:=anchor, sa.transform:=trans, node.id))
    }

//    d3.select(self.frameElement).style("height", diameter - 150 + "px");

    container.appendChild(svgTag(aspectRatio)(styles.greyBackground,
      st.g(sa.transform:= transform.translate(radius, radius), linkTags, nodeTags)).render)
  }
}
