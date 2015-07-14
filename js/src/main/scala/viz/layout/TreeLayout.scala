package viz.layout

trait TreeLayout extends HierarchyLayout[ExpandedTree] {

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#separation]]
   */
  def separation(fn: (ExpandedTree, ExpandedTree) => Double): TreeLayout

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#size]]
   */
  def size(x: Double, y: Double): TreeLayout

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#nodeSize]]
   */
  def nodeSize(x: Double, y: Double): TreeLayout

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#nodes]]
   */
  def nodes(root: Tree): Seq[ExpandedTree] = apply(root)
}
