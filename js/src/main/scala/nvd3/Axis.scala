package nvd3

import scala.scalajs.js

trait Axis extends js.Object {
  def axisLabel(label: String): Axis = js.native

  def tickFormat(format: js.Object): Axis = js.native
}
