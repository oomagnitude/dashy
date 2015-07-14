package viz.layout

import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait Tree {
  def children: UndefOr[Seq[Tree]]

  def id: String
}

@JSExportAll
case class TreeLink[N](source: N, target: N)

/**
  * derive a custom hierarchical layout implementation
  * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout]]
  */
trait HierarchyLayout[N] {
  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#_hierarchy]]
   */
  def apply(root: Tree): Seq[N]

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#links]]
   */
  def links(root: Tree): Seq[TreeLink[N]]
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