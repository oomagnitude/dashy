package viz.shape

trait ArcDatum[T] {
  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc_outerRadius]]
   */
  def outerRadius(item: T): Double

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc_innerRadius]]
   */
  def innerRadius(item: T): Double

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc_startAngle]]
   */
  def startAngle(item: T): Double

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc_endAngle]]
   */
  def endAngle(item: T): Double

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc_cornerRadius]]
   */
  def cornerRadius(item: T): Double = 0

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc_padRadius]]
   */
  def padRadius(item: T): Double = {
    val inner = innerRadius(item)
    val outer = outerRadius(item)
    Math.sqrt(inner * inner + outer * outer)
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc_padAngle]]
   */
  def padAngle(item: T): Double
}

/**
 * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc]]
 */
trait Arc {

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#_arc]]
   */
  def apply[T: ArcDatum](datum: T): String

  /**
   * [[https://github.com/mbostock/d3/wiki/SVG-Shapes#arc_centroid]]
   */
  def centroid[T: ArcDatum](datum: T): String
}
