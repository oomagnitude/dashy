package viz.layout

case class Coordinate(x: Double, y: Double)

trait TreeLayout extends HierarchyLayout[Coordinate] with Sized {

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#separation]]
   */
  def separation(fn: (NodeStructure, NodeStructure) => Double): TreeLayout

  /**
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout#nodeSize]]
   */
  def nodeSize(x: Double, y: Double): TreeLayout
}
