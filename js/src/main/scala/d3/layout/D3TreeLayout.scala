package d3.layout
import d3._
import viz.layout._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr

class D3TreeLayout extends TreeLayout {
  private[this] val layout: js.Dynamic = d3.layout.tree()

  override def separation(fn: (ExpandedNode, ExpandedNode) => Double): this.type = {
    layout.separation({(a: js.Dynamic, b: js.Dynamic) => fn(a.asInstanceOf[ExpandedNode], b.asInstanceOf[ExpandedNode])})
    this
  }

  override def links(root: Tree): Seq[TreeLink[ExpandedNode]] = {
    val nodes = computeNodes(root)
    layout.links(nodes)
      .asInstanceOf[js.Array[js.Dynamic]]
      .map(link => TreeLink(toExpandedNode(link.source), toExpandedNode(link.target)))
  }

  override def apply(root: Tree): Seq[ExpandedNode] = computeNodes(root).map(toExpandedNode)

  private def toExpandedNode(node: js.Dynamic): ExpandedNode = {
    val children = node.children.asInstanceOf[UndefOr[js.Array[js.Dynamic]]]
    val parent = node.parent.asInstanceOf[UndefOr[js.Dynamic]]
    val numChildren = children.map(_.size).getOrElse(0)
    ExpandedNode(node.id.asInstanceOf[String], node.depth.asInstanceOf[Int], node.x.asInstanceOf[Double],
      node.y.asInstanceOf[Double], numChildren, parent.isEmpty)
  }

  private def computeNodes(root: Tree): js.Array[js.Dynamic] =
    layout(convertToLiteral(root)).asInstanceOf[js.Array[js.Dynamic]]

  private def convertToLiteral(root: Tree): js.Dynamic = {
    val jsObj = literal(id = root.id)
    if (root.children.nonEmpty) {
      val literalChildren = root.children.get.map(convertToLiteral)
      jsObj.children = literalChildren.toJSArray
    }
    jsObj
  }

  override def size(x: Double, y: Double): this.type = {
    layout.size(js.Array(x, y))
    this
  }

  override def nodeSize(x: Double, y: Double): this.type = {
    layout.nodeSize(js.Array(x, y))
    this
  }
}
