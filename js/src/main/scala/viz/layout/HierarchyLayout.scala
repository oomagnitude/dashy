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

case class StufNThings(id: String, depth: Int, parentId: Option[String], childrenIds: Seq[String])

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
   * [[https://github.com/mbostock/d3/wiki/Partition-Layout#nodes]]
   */
  def nodes(root: Tree): Seq[N] = apply(root)

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#links]]
   */
  def links(root: Tree): Seq[TreeLink[N]]
}

trait Sized {
  def size(x: Double, y: Double): this.type
}

trait Sorted {
  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#sort]]
   */
  def sort(comparator: (StufNThings, StufNThings) => Double): this.type

  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#sort]]
   */
  def disableSort(): this.type
}

trait Value {
  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#value]]
   */
  def value(fn: StufNThings => Double): this.type
}

trait Revalue {
  /**
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout#revalue]]
   */
  def revalue(root: StufNThings): this.type
}