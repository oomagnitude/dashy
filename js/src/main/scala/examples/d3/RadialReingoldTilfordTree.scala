package examples.d3

import com.oomagnitude.css._
import d3.all._
import examples.d3.Data._
import org.scalajs.dom.html
import svg._
import upickle.{default => upickle}
import viz.layout.NodeStructure

import scala.scalajs.js.annotation.JSExport
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}

@JSExport
object RadialReingoldTilfordTree {

  @JSExport
  def main(container: html.Div): Unit = {
    val aspectRatio = 1.185
    val (width, _) = viz.dimensions(aspectRatio)
    val diameter = width
    val radius = diameter / 2.0

    val treeData = upickle.read[TreeNode](treeJson)
    val layout = d3.layout.tree
      .size(360, radius - 120)
      .separation({(a: NodeStructure, b: NodeStructure) => (if(a.parentId == b.parentId) 1.0 else 2.0) / a.depth.toDouble})
      .layout(treeData)

    val diagonal = d3.shape.diagonalRadial
      .projection({(x: Double, y: Double)=> (y, x / 180 * Math.PI) })


    val linkTags = layout.links.map {link => st.path(styles.treeLink, sa.d:=diagonal(link))}
    val nodeTags = layout.nodes.map { node =>
      val (anchor, trans) =
        if(node.layout.x < 180) ("start", transform.translate(8))
        else ("end", transform.transforms(transform.rotate(180), transform.translate(-8)))
      st.g(styles.treeNode, sa.transform:=
          transform.transforms(transform.rotate(node.layout.x - 90), transform.translate(node.layout.y)),
        st.circle(styles.treeNodeCircle, sa.r:= 2.5),
        st.text(sa.dy:=".31em", sa.textAnchor:=anchor, sa.transform:=trans, node.id))
    }

    container.appendChild(svgTag(aspectRatio)(styles.greyBackground,
      st.g(sa.transform:= transform.translate(radius, radius), linkTags, nodeTags)).render)
  }
}
