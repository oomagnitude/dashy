package d3.layout

import d3._
import viz.layout._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

class D3PartitionLayout extends PartitionLayout {
  private[this] val layout = d3.layout.partition()
  private[this] var treeStructure = Map.empty[String, NodeStructure]

  private def toNode(d: js.Dynamic): (String, NodeStructure) = {
    val id = d.id.asInstanceOf[String]
    (id, treeStructure(id))
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#value]]
   */
  override def value(fn: (String, NodeStructure) => Double): this.type = {
    layout.value({d: js.Dynamic =>
      val (id, structure) = toNode(d)
      fn(id, structure)
    })
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#value]]
   */
  override def value(const: Double): this.type = {
    layout.value({d: js.Dynamic => const})
    this
  }

  override def layout(root: Tree): Hierarchy[CoordinateAndExtent] = {
    treeStructure = parentAndChildrenMap(root, depth = 0, None, Map.empty)
    computeLayout[CoordinateAndExtent](layout, root, treeStructure) {
      node => upicklejs.read[CoordinateAndExtent](literal(x = node.x, y = node.y, dx = node.dx, dy = node.dy))
    }
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
  override def sort(comparator: (NodeStructure, NodeStructure) => Double): this.type = {
    layout.sort({(a: js.Dynamic, b: js.Dynamic) => comparator(toNode(a)._2, toNode(b)._2)})
    this
  }

  override def size(x: Double, y: Double): this.type = {
    layout.size(js.Array(x, y))
    this
  }

}
