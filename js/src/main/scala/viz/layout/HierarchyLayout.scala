package viz.layout

import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExportAll, JSExport}

trait Tree {
  @JSExport("children")
  // needed for traversal
  def children: UndefOr[Seq[Tree]]

  @JSExport("id")
  def id: String
}

//trait ExpandedTree {
//  @JSExport("id")
//  def id: String
//
//  @JSExport("parent")
//  def parent: UndefOr[ExpandedTree]
//
//  @JSExport("depth")
//  def depth: Int
//
//  @JSExport("x")
//  def x: Double
//
//  @JSExport("y")
//  def y: Double
//
//  @JSExport
//  def numChildren: Int
//
//  def isLeaf: Boolean = numChildren < 1
//
//  override def toString: String = {
//    s"ExpandedTree(id = $id, depth = $depth, x = $x, y = $y, hasParent = ${if (parent.isDefined) true else false}})"
//  }
//}

@JSExportAll
case class ExpandedTree(id: String, depth: Int, x: Double, y: Double, numChildren: Int, isRoot: Boolean) {
  def isLeaf: Boolean = numChildren <= 0
}

@JSExportAll
case class HierarchyLink[N](source: N, target: N)

/**
  * derive a custom hierarchical layout implementation
  * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout]]
  */
trait HierarchyLayout[N] {
  // nodes: need to get x and y, plus depth
  // links: need to get source and target (using parent -> this)

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#_hierarchy]]
   */
  def apply(root: Tree): Seq[N]

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#links]]
   */
  def links(root: Tree): Seq[HierarchyLink[N]]

//  /**
//   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#children]]
//   */
//  def children(accessor: N => Seq[N]): this.type
}

//// TODO: come up with comparator for sorting. off by default.
//trait Sorted {
//  /**
//   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#sort]]
//   */
//  def sort(comparator: (N, N) => Double): this.type
//}
//
//// TODO: come up with impl for value. Only affects Treemaps
//trait Value {
//  /**
//   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#value]]
//   */
//  def value(fn: N => Double): this.type
//
//  /**
//   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#revalue]]
//   */
//  def revalue(root: N): this.type
//}