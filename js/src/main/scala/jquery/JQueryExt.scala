package jquery

import org.scalajs.jquery.JQuery

import scala.language.implicitConversions
import scala.scalajs.js

object JQueryExt {
  implicit def jq2Ext(jq: JQuery): JQueryExt = jq.asInstanceOf[JQueryExt]
}

trait JQueryExt extends JQuery {
  def typeahead(config: js.Dynamic, data: js.Dynamic): this.type = js.native

  def typeahead(key: String): this.type = js.native

  def typeahead(key: String, value: String): this.type  = js.native

  def tooltip(options: js.Dynamic): this.type = js.native
}
