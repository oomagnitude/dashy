package viz.interpolate

trait Interpolate {
  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolate]]
   */
  def interpolate[T](a: T, b: T): Interpolator[T]

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolateNumber]]
   */
  def interpolateNumber(a: Double, b: Double): Interpolator[Double]

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolateRound]]
   */
  def interpolateRound(a: Double, b: Double): Interpolator[Double]

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolateString]]
   */
  def interpolateString(a: String, b: String): Interpolator[String]

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolateRgb]]
   */
  def interpolateRgb(a: String, b: String): Interpolator[String]

  // TODO: hsl, lab, hcl, array, object, transform, zoom
}
