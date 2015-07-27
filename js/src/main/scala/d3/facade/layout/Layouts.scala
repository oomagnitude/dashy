package d3.facade.layout

import scalajs.js

/**
 * [[https://github.com/mbostock/d3/wiki/Layouts]]
 */
trait Layouts extends js.Object {
  /**
   * position linked nodes using physical simulation
   * [[https://github.com/mbostock/d3/wiki/Force-Layout]]
   * @return
   */
  def force(): ForceLayout = js.native
}