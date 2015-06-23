package jquery

import org.scalajs.jquery.JQuery

import scala.language.implicitConversions
import scala.scalajs.js

object JQueryExt {
  implicit def jq2Combobox(jq: JQuery): JQueryExt = jq.asInstanceOf[JQueryExt]

  def refresh(jq: JQuery): Unit = {
    jq.combobox("clearElement")
    jq.combobox("clearTarget")
    jq.combobox("refresh")
  }
}

trait JQueryExt extends JQuery {
  def combobox(): this.type = js.native

  def combobox(operation: String): this.type = js.native
}
