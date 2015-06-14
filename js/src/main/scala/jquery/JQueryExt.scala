package jquery


import biz.enef.angulate.core.AugmentedJQLite
import org.scalajs.jquery.JQuery

import scala.scalajs.js
import scala.language.implicitConversions

object JQueryExt {
  implicit def jq2Combobox(jq: JQuery): JQueryExt = jq.asInstanceOf[JQueryExt]

  implicit def jqLite2Combobox(jq: AugmentedJQLite): JQueryExt = jq.asInstanceOf[JQueryExt]

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
