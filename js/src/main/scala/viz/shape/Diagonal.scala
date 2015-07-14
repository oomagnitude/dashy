package viz.shape

import viz.layout.{TreeLink, TreeNode}

trait Diagonal {
  // TODO: change to type T instead of TreeNode

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def apply(datum: TreeLink[TreeNode]): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def apply(datum: TreeLink[TreeNode], index: Int): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def projection(fn: (Double, Double) => (Double, Double)): this.type
}
