package combobox


import biz.enef.angulate.core.AugmentedJQLite
import org.scalajs.jquery.JQuery

import scala.scalajs.js
import scala.language.implicitConversions

object Combobox {
  implicit def jq2Combobox(jq: JQuery): Combobox = jq.asInstanceOf[Combobox]

  implicit def jqLite2Combobox(jq: AugmentedJQLite): Combobox = jq.asInstanceOf[Combobox]
}

trait Combobox extends JQuery {
  def combobox(): this.type = js.native
}
