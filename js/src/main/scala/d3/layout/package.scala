package d3

import viz.layout._

import scala.scalajs.js

package object layout {
  private[layout] def computeLayout[N](layout: js.Dynamic, root: Tree, structure: Map[String, NodeStructure])(toLayout: js.Dynamic => N): Hierarchy[N] = {
    val rawNodes = layout(upicklejs.write(root)).asInstanceOf[js.Array[js.Dynamic]]
    val layouts = rawNodes.map { node => node.id.asInstanceOf[String] -> toLayout(node)}
    val nodes = layouts.map {case (id, li) => id -> LayoutNode(id, structure(id), li)}
    val nodeMap = nodes.toMap
    val links = nodes.flatMap {
      case (parentId, node) =>
        node.structure.childrenIds.map { childId => TreeLink(node, nodeMap(childId)) }
    }
    Hierarchy(nodes.map(_._2).toSeq, links.toSeq)
  }
}
