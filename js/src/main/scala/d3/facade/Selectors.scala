package d3.facade

import org.scalajs.dom.EventTarget
import selection.Selection

import scala.scalajs.js

trait Selectors extends js.Object {
  def select(): Selection = js.native
  def select(selector: String): Selection = js.native
  def select(element: EventTarget): Selection = js.native
  def selectAll(selector: String): Selection = js.native
  def selectAll(elements: js.Array[EventTarget]): Selection = js.native
}