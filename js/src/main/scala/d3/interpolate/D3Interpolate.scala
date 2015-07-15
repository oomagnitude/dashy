package d3.interpolate

import d3._
import viz.interpolate.{Interpolate, Interpolator}

object D3Interpolate extends Interpolate {
  override def interpolate[T](a: T, b: T): Interpolator[T] = new D3Interpolator[T](a, b)

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolateRound]]
   */
  override def interpolateRound(a: Double, b: Double): Interpolator[Double] =
    new D3Interpolator[Double](d3.interpolateRound(a,b))

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolateNumber]]
   */
  override def interpolateNumber(a: Double, b: Double): Interpolator[Double] =
    new D3Interpolator[Double](d3.interpolateNumber(a,b))

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolateString]]
   */
  override def interpolateString(a: String, b: String): Interpolator[String] =
    new D3Interpolator[String](d3.interpolateString(a,b))

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_interpolateRgb]]
   */
  override def interpolateRgb(a: String, b: String): Interpolator[String] =
    new D3Interpolator[String](d3.interpolateRgb(a,b))
}
