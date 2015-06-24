package com.oomagnitude.view

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

  def apply(placeHolder: String, options: Rx[List[SelectOption]], selectedVar: Var[String]): html.Element = {
    val tInput = input(cls:="typeahead", style:= "max-height: 150px; overflow-y: auto;", `type`:="text", placeholder:=placeHolder).render
    // style: "max-height: 150px; overflow-y: auto;"

    Obs(options) {
      selectedVar() = ""
      val jTypeahead = jQuery(tInput)
      jTypeahead.typeahead("destroy")

      val suggestionEngine = jsnew(Bloodhound)(l(
        identify = {o: js.Dynamic =>  o.value},
        datumTokenizer = Bloodhound.tokenizers.obj.whitespace("name"),
        queryTokenizer = Bloodhound.tokenizers.whitespace,
        dupDetector = {(a: js.Dynamic, b: js.Dynamic) => a.value == b.value },
        local = options().toJSArray.map(_.toJs)
      ))

      def engineWithDefaults(q: String, sync: js.Dynamic, async: js.Dynamic) {
        if (q.trim.isEmpty || q.trim == placeHolder) sync(options().map(_.toJs).toJSArray)
        else suggestionEngine.search(q, sync, async)
      }

      jTypeahead.typeahead(l(minLength = 0),
        l(limit = 1000, displayKey = "name", source = engineWithDefaults _)
      ).on("typeahead:select", { (_: js.Dynamic, datum: js.Dynamic) =>
        selectedVar() = datum.value.toString
      }).on("typeahead:change", {() =>
        val textInput = jTypeahead.typeahead("val").toString
        options().find(_.name == textInput).foreach {opt => selectedVar() = opt.value}
      }).on("click", {() => selectedVar() = ""})
    }

    Obs(selectedVar) {
      if (selectedVar().trim.isEmpty) jQuery(tInput).typeahead("val", "")
      else options().find(_.value == selectedVar()).foreach {
        opt => jQuery(tInput).typeahead("val", opt.name)
      }
    }

    div(cls:="scrollable-dropdown-menu", tInput).render
  }

}
