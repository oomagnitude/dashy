package d3.layout
import d3._
import viz.layout._

import scala.scalajs.js

class D3TreeLayout extends TreeLayout {
  private[this] val layout: js.Dynamic = d3.layout.tree()
  private[this] var structure = Map.empty[String, NodeStructure]

  private def toNode(d: js.Dynamic): (String, NodeStructure) = {
    val id = d.id.asInstanceOf[String]
    (id, structure(id))
  }

  override def separation(fn: (NodeStructure, NodeStructure) => Double): this.type = {
    layout.separation({(a: js.Dynamic, b: js.Dynamic) =>
      fn(toNode(a)._2, toNode(b)._2)
    })
    this
  }

  override def layout(root: Tree): Hierarchy[Coordinate] = {
    structure = parentAndChildrenMap(root, depth = 0, None, Map.empty)
    computeLayout(layout, root, structure) {
      node => Coordinate(x = node.x.asInstanceOf[Double], y = node.y.asInstanceOf[Double])
    }
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
