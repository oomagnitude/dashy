package viz.layout

import viz.layout.TreeLayout.ParentIdAndDepth

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
case class TreeNode(id: String, depth: Int, x: Double, y: Double, numChildren: Int, parentId: Option[String]) {
  def isLeaf: Boolean = numChildren <= 0
  def isRoot: Boolean = parentId.isEmpty
}

object TreeLayout {
  type ParentIdAndDepth = (Option[String], Int)
}

trait TreeLayout extends HierarchyLayout[TreeNode] with Sized {

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#separation]]
   */
  def separation(fn: (ParentIdAndDepth, ParentIdAndDepth) => Double): TreeLayout

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#nodeSize]]
   */
  def nodeSize(x: Double, y: Double): TreeLayout

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#nodes]]
   */
  override def nodes(root: Tree): Seq[TreeNode] = apply(root)
}
