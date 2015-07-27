package d3.facade.scale

import scala.scalajs.js

trait Scale[S] extends js.Object {
  def apply(value: js.Any): js.Dynamic = js.native
  def domain(): js.Array[js.Any] = js.native
  def domain(values: js.Array[js.Any]): S = js.native
  def range(): js.Array[js.Any] = js.native
  def range(values: js.Array[js.Any]): S = js.native
  def copy(): S = js.native
}

trait OrdinalScale extends Scale[OrdinalScale] {
  def rangePoints(interval: js.Array[js.Any], padding: Double = js.native): OrdinalScale = js.native
  def rangeRoundPoints(interval: js.Array[js.Any], padding: Double = js.native): OrdinalScale = js.native
  def rangeBands(interval: js.Array[js.Any], padding: Double = js.native, outerPadding: Double = js.native): OrdinalScale = js.native
  def rangeRoundBands(interval: js.Array[js.Any], padding: Double = js.native, outerPadding: Double = js.native): OrdinalScale = js.native
  def rangeBand(): Double = js.native
  def rangeExtent(): js.Array[Double] = js.native
}
