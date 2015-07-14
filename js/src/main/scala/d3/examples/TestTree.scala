package d3.examples

import scala.scalajs.js
import scala.scalajs.js.UndefOr

trait TestTree extends js.Object {
  def children: UndefOr[js.Array[TestTree]] = js.native

  def name: String = js.native
}