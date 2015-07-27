package d3.facade.layout

import scalajs.js

trait ForceLayout extends js.Object {
  def apply(): ForceLayout = js.native
  def size(): Double = js.native
  def size(mysize: js.Array[Double]): ForceLayout = js.native
  def size(accessor: js.Function2[js.Any, Double, js.Any]): ForceLayout = js.native
  def linkDistance(): Double = js.native
  def linkDistance(number: Double): ForceLayout = js.native
  def linkDistance(accessor: js.Function2[js.Any, Double, Double]): ForceLayout = js.native
  def linkStrength(): Double = js.native
  def linkStrength(number: Double): ForceLayout = js.native
  def linkStrength(accessor: js.Function2[js.Any, Double, Double]): ForceLayout = js.native
  def friction(): Double = js.native
  def friction(number: Double): ForceLayout = js.native
  def friction(accessor: js.Function2[js.Any, Double, Double]): ForceLayout = js.native
  def alpha(): Double = js.native
  def alpha(number: Double): ForceLayout = js.native
  def alpha(accessor: js.Function2[js.Any, Double, Double]): ForceLayout = js.native
  def charge(): Double = js.native
  def charge(number: Double): ForceLayout = js.native
  def charge(accessor: js.Function2[js.Any, Double, Double]): ForceLayout = js.native
  def theta(): Double = js.native
  def theta(number: Double): ForceLayout = js.native
  def theta(accessor: js.Function2[js.Any, Double, Double]): ForceLayout = js.native
  def gravity(): Double = js.native
  def gravity(number: Double): ForceLayout = js.native
  def gravity(accessor: js.Function2[js.Any, Double, Double]): ForceLayout = js.native
  def links(): js.Array[GraphLinkForce] = js.native
  def links(arLinks: js.Array[GraphLinkForce]): ForceLayout = js.native
  def nodes(): js.Array[GraphNodeForce] = js.native
  def nodes(arNodes: js.Array[GraphNodeForce]): ForceLayout = js.native
  def start(): ForceLayout = js.native
  def resume(): ForceLayout = js.native
  def stop(): ForceLayout = js.native
  def tick(): ForceLayout = js.native
  def on(`type`: String, listener: js.Function): ForceLayout = js.native
  var drag: js.Function = js.native
}

trait GraphNodeForce extends js.Object {
  var index: Double = js.native
  var x: Double = js.native
  var y: Double = js.native
  var px: Double = js.native
  var py: Double = js.native
  var fixed: Boolean = js.native
  var weight: Double = js.native
}

trait GraphLinkForce extends js.Object {
  var source: GraphNodeForce = js.native
  var target: GraphNodeForce = js.native
}