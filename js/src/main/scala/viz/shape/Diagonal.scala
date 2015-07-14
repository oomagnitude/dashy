package viz.shape

import viz.layout.{ExpandedTree, HierarchyLink}

trait Diagonal {
  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def apply(datum: HierarchyLink[ExpandedTree]): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def apply(datum: HierarchyLink[ExpandedTree], index: Int): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def projection(fn: (Double, Double) => (Double, Double)): this.type
}
