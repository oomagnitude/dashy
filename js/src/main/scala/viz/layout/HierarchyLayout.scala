package viz.layout

import upickle.{Js, default => upickle}

object Tree {
  implicit val writer = new upickle.Writer[Tree] {
    override def write0: (Tree) => Js.Value = {
      case tree if tree.children.nonEmpty =>
        Js.Obj("id" -> Js.Str(tree.id), "children" -> Js.Arr(tree.children.map(write0):_*))
      case tree =>
        Js.Obj("id" -> Js.Str(tree.id))
    }
  }
}

trait Tree {
  def children: Seq[Tree]
  def id: String
}

case class TreeLink[N](source: N, target: N)

case class NodeStructure(parentId: Option[String], childrenIds: Seq[String], depth: Int)

case class LayoutNode[T](id: String, structure: NodeStructure, layout: T) {
  val isLeaf: Boolean = structure.childrenIds.isEmpty
  val isRoot: Boolean = structure.parentId.isEmpty
}

case class Hierarchy[N](nodes: Seq[LayoutNode[N]], links: Seq[TreeLink[LayoutNode[N]]])

/**
  * derive a custom hierarchical layout implementation
  * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout]]
  */
trait HierarchyLayout[N] {
  /**
   * Computes the layout of a given tree
   *
   * Combines these operations into one:
   * [[https://github.com/mbostock/d3/wiki/Partition-Layout#nodes]]
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#links]]
   *
   * @param root the root node of the tree
   * @return the layout, including x/y coordinates of the nodes and links connecting the nodes
   */
  def layout(root: Tree): Hierarchy[N]

  protected def parentAndChildrenMap(root: Tree, depth: Int, parentId: Option[String],
                                   nodeMap: Map[String, NodeStructure]): Map[String, NodeStructure] = {
    (if (root.children.isEmpty) nodeMap
    else {
      root.children.foldLeft(nodeMap) { (map, child) =>
        parentAndChildrenMap(child, depth + 1, Some(root.id), map)
      }
    }) + (root.id -> NodeStructure(parentId, root.children.map(_.id), depth))
  }
}

trait Sized {
  def size(x: Double, y: Double): this.type
}

trait Sorted {
  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#sort]]
   */
  def sort(comparator: (NodeStructure, NodeStructure) => Double): this.type

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#sort]]
   */
  def disableSort(): this.type
}

trait Value {
  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#value]]
   */
  def value(fn: (String, NodeStructure) => Double): this.type

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#value]]
   */
  def value(const: Double): this.type
}

trait Revalue {
  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#revalue]]
   */
  def revalue(root: NodeStructure): this.type
}