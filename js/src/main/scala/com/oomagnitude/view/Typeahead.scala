package com.oomagnitude.view

import com.oomagnitude.model.SelectOptions
import jquery.JQueryExt._
import org.scalajs.dom.html
import org.scalajs.jquery._
import rx._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l, newInstance => jsnew}
import scala.scalajs.js.JSConverters._
import scalatags.JsDom.all._

object Typeahead {
  val Bloodhound = js.Dynamic.global.Bloodhound

  def apply[T](placeHolder: String, model: SelectOptions[T]): html.Element = {
    val tInput = input(cls:="typeahead", style:= "max-height: 250; overflow-y: auto;", `type`:="text",
      placeholder:=placeHolder).render
    val jTypeahead = jQuery(tInput)

    Obs(model.selectOptions) {
      model.selectedId() = None
      jTypeahead.typeahead("destroy")

      val suggestionEngine = jsnew(Bloodhound)(l(
        identify = {o: js.Dynamic =>  o.value},
        datumTokenizer = Bloodhound.tokenizers.obj.whitespace("name"),
        queryTokenizer = Bloodhound.tokenizers.whitespace,
        dupDetector = {(a: js.Dynamic, b: js.Dynamic) => a.value == b.value },
        local = model.selectOptions().toJSArray.map(_.toJs)
      ))

      def engineWithDefaults(q: String, sync: js.Dynamic, async: js.Dynamic) {
        if (q.trim.isEmpty || q.trim == placeHolder) sync(model.selectOptions().toJSArray.map(_.toJs))
        else suggestionEngine.search(q, sync, async)
      }

      jTypeahead.typeahead(l(minLength = 0),
        l(limit = 1000, displayKey = "name", source = engineWithDefaults _)
      ).on("typeahead:select", { (_: js.Dynamic, datum: js.Dynamic) =>
        model.selectedId() = Some(datum.value.toString)
      }).on("typeahead:change", {() =>
        val textInput = jTypeahead.typeahead("val").toString
        model.selectOptions().find(_.name == textInput).foreach(opt => model.selectedId() = Some(opt.id))
      }).on("click", {() => model.selectedId() = None})
    }

    Obs(model.selectedId) {
      model.selectedId() match {
        case None => jQuery(tInput).typeahead("val", "")
        case Some(text) => model.selectOptions().find(_.id == text).foreach {
          opt => jQuery(tInput).typeahead("val", opt.name)
        }
      }
    }

    div(cls:="scrollable-dropdown-menu", tInput).render
  }

}
