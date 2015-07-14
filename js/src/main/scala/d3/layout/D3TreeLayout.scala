package d3.layout
import d3._
import viz.layout.TreeLayout.ParentIdAndDepth
import viz.layout._

import scala.scalajs.js

class D3TreeLayout extends TreeLayout {
  private[this] val layout: js.Dynamic = d3.layout.tree()

  override def separation(fn: (ParentIdAndDepth, ParentIdAndDepth) => Double): this.type = {
    layout.separation({(a: js.Dynamic, b: js.Dynamic) =>
      fn((parentId(a), a.depth.asInstanceOf[Int]), (parentId(b), b.depth.asInstanceOf[Int]))
    })
    this
  }

  override def links(root: Tree): Seq[TreeLink[TreeNode]] = {
    val nodes = computeNodes(layout, root)
    layout.links(nodes)
      .asInstanceOf[js.Array[js.Dynamic]]
      .map(link => TreeLink(toExpandedNode(link.source), toExpandedNode(link.target)))
  }

  override def apply(root: Tree): Seq[TreeNode] = computeNodes(layout, root).map(toExpandedNode)

  private def toExpandedNode(node: js.Dynamic): TreeNode = {
    val children = childrenIds(node)
    val parent = parentId(node)
    TreeNode(node.id.asInstanceOf[String], node.depth.asInstanceOf[Int], node.x.asInstanceOf[Double],
      node.y.asInstanceOf[Double], children.size, parent)
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
