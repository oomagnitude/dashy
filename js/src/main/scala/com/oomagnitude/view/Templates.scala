package com.oomagnitude.view

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.{Event, MouseEvent}
import rx._

import scala.language.implicitConversions
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

object Templates {
  import com.oomagnitude.rx.Rxs._

  def pauseButton(paused: Var[Boolean], playingText: String = "Pause", pausedText: String = "Resume"): html.Button = {
    val buttonText = Var(if (paused()) pausedText else playingText)
    bs.btnDefault(buttonText.asFrag,
      onclick:= { e: MouseEvent =>
        if (paused()) {
          buttonText() = playingText
          paused() = false
        } else {
          buttonText() = pausedText
          paused() = true
        }
    }).render
  }

  def removeButton(remove: () => Unit): html.Button = {
    bs.btnDefault(onclick:= {e: MouseEvent => remove()}, bs.removeCircle).render
  }

  def textInput(value: Var[Option[String]]): html.Element = {
    val element = input(`type`:="text").render
    element.onchange = {(e: Event) => if (element.value.trim.nonEmpty) value() = Some(element.value) else value() = None}
    Obs(value) {
      if (value().isEmpty) element.value = ""
      else element.value = value().get
    }
    element
  }

  def numberInput(value: Var[Int]): html.Element = {
    val element = input(`type`:="number").render
    element.onchange = {(e: Event) => value() = element.value.toInt}
    element
  }

  def tag(label: String, remove: MouseEvent => Unit): html.Element = {
    val anchor = a(onclick:= remove, i(cls:="remove glyphicon glyphicon-remove-sign glyphicon-white"))
    div(cls:="row", span(cls:="tag label label-info", label, anchor)).render
  }

  def radio(radioName: String, options: List[SelectOption], checkedValue: Var[String]): html.Element = {
    var first = true
    val optionTags: List[TypedTag[_]] = options.map {
      opt =>
        var inp = input(`type`:="radio", name:=radioName, id:=opt.name, value:=opt.id,
          onchange:= {e: dom.Event => checkedValue() = opt.id})
        if (first) inp = inp(checked:="checked")
        first = false
        bs.formGroup(bs.col12(inp, label(`for`:=opt.name, opt.name)))
    }
    form(optionTags: _*).render
  }
}
