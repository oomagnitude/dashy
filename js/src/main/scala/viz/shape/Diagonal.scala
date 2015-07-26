package viz.shape

import viz.layout.{Coordinate, TreeLink}

trait Diagonal {
  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def apply(datum: TreeLink[Coordinate]): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def apply(datum: TreeLink[Coordinate], index: Int): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_diagonal]]
   */
  def projection(fn: (Double, Double) => (Double, Double)): this.type
}
