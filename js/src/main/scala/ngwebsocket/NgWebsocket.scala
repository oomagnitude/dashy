package ngwebsocket

import scala.scalajs.js

trait NgWebsocket extends js.Object {
  def $on(event: String, fn: js.Function): Unit = js.native

  def $close(): Unit = js.native
}
