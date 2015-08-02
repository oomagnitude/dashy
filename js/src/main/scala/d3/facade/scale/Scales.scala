package d3.facade.scale

import scalajs.js

trait Scales extends js.Object {
  def ordinal(): OrdinalScale = js.native
  def category10(): OrdinalScale = js.native
  def category20(): OrdinalScale = js.native
  def category20b(): OrdinalScale = js.native
  def category20c(): OrdinalScale = js.native
}