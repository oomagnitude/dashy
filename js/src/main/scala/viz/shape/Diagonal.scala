package viz.shape

import viz.layout.{ExpandedNode, TreeLink}

trait Diagonal {
  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def apply(datum: TreeLink[ExpandedNode]): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def apply(datum: TreeLink[ExpandedNode], index: Int): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def projection(fn: (Double, Double) => (Double, Double)): this.type
}
