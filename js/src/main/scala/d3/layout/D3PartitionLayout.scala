package d3.layout

import d3._
import viz.layout._

import scala.scalajs.js

class D3PartitionLayout extends PartitionLayout {
  private[this] val layout = d3.layout.partition()

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#value]]
   */
  override def value(fn: (StufNThings) => Double): this.type = {
    layout.value({d: js.Dynamic => fn(extractNode(d))})
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#links]]
   */
  override def links(root: Tree): Seq[TreeLink[PartitionNode]] = {
    val nodes = computeNodes(layout, root)
    layout.links(nodes)
      .asInstanceOf[js.Array[js.Dynamic]]
      .map(link => TreeLink(toExpandedNode(link.source), toExpandedNode(link.target)))
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#_hierarchy]]
   */
  override def apply(root: Tree): Seq[PartitionNode] = computeNodes(layout, root).map(toExpandedNode)

  private def toExpandedNode(node: js.Dynamic): PartitionNode = {
    val children = childrenIds(node)
    val parent = parentId(node)
    PartitionNode(
      id = node.id.asInstanceOf[String],
      depth = node.depth.asInstanceOf[Int],
      x = node.x.asInstanceOf[Double], y = node.y.asInstanceOf[Double],
      dx = node.dx.asInstanceOf[Double], dy = node.dy.asInstanceOf[Double],
      value = node.value.asInstanceOf[Double],
      children, parent)
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#sort]]
   */
  override def disableSort(): this.type = {
    layout.sort(null)
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#sort]]
   */
  override def sort(comparator: (StufNThings, StufNThings) => Double): this.type = {
    layout.sort({(a: js.Dynamic, b: js.Dynamic) => comparator(extractNode(a), extractNode(b))})
    this
  }

  override def size(x: Double, y: Double): this.type = {
    layout.size(js.Array(x, y))
    this
  }

}
