package viz.interpolate

trait Interpolator[T] {
  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#_interpolate]]
   */
  def apply(t: Double): T
}
