package com.oomagnitude.rx.view

import com.oomagnitude.api.DataSourceId
import com.oomagnitude.rx.model.ExperimentSelection
import combobox.Combobox
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.MouseEvent
import org.scalajs.jquery._
import rx._

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import language.implicitConversions

object Templates {
  import com.oomagnitude.rx.Rxs._

  object bootstrap {
    def row = div(cls:="row")

    def col2 = div(cls:="col-lg-2")

    def col10 = div(cls:="col-lg-10")

    def col12 = div(cls:="col-lg-12")

    def well = div(cls:="well well-sm")

    def container = div(cls:="container")

    def panel = div(cls:="panel panel-default")

    def panelHeading = div(cls:="panel-heading")

    def panelBody = div(cls:="panel-body")

    def removeCircle = span(cls:="glyphicon glyphicon-remove-circle")

    def center = div(cls:="center-block")

    def formGroup = div(cls:="form-group")

    def formInline = div(cls:="form-inline")

    def formHorizontal = div(cls:="form-horizontal")

    def btnGroup = div(cls:="btn-group", role:="group")

    def btnPrimary = button(cls:="btn btn-primary", `type`:="button")

    def btnDefault = button(cls:="btn btn-default", `type`:="button")
  }
  
  val bs = bootstrap
  
  object Dynamic {
    def combobox(options: Rx[List[(String,String)]], selectedVar: Var[String]): html.Select = {
      val choices = Rx {
        options() match {
          case first :: rest =>
            option(selected:="selected", value:=first._1, first._2) :: rest.map(kv => option(value:=kv._1, kv._2))
          case _ => Nil
        }
      }

      val selectTag: html.Select = select(cls:="combobox form-control",
        choices.asFrags({ parent =>
          // Reset the selected item in this list
          selectedVar() = ""
          Combobox.refresh(jQuery(parent))
        })
      ).render

      selectTag.onchange = { e: dom.Event => selectedVar() = selectTag.value }

      selectTag
    }
    
    def pauseButton(paused: Var[Boolean]): html.Button = {
      val buttonText = Var("Pause")
      val pause = bs.btnDefault(buttonText.asFrag).render

      pause.onclick = { e: MouseEvent =>
        if (paused()) {
          buttonText() = "Pause"
          paused() = false
        } else {
          buttonText() = "Resume"
          paused() = true
        }
      }
      pause
    }

    def ulist(items: Rx[List[String]]): html.UList = ul(toListItems(items).asFrags()).render

    def olist(items: Rx[List[String]]): html.OList = ol(toListItems(items).asFrags()).render

    def toListItems(items: Rx[List[String]]): Rx[List[TypedTag[html.LI]]] = Rx {items().map(li(_))}

    def tagGroup(tags: Var[List[DataSourceId]]): html.Div = {
      implicit val idToTag: DataSourceId => Frag = {id => tag(id.toString, {event => tags() = tags().filterNot(_ == id)})}
      div(tags.asFrags()).render
    }
  }

  def experimentForm(selection: ExperimentSelection, tags: Var[List[DataSourceId]],
                     addChart: MouseEvent => Unit): html.Element = {
    import Dynamic._
    val experimentsDisplay = Rx {("", "select an experiment") :: selection.experiments().sorted.map(id => (id, id))}
    val datesDisplay = Rx {
      ("", "select a date") :: selection.dates().sorted(Ordering.String.reverse).map(id =>
        (id, s"${moment.humanReadableDurationFromNow(id)} (${moment.calendarDate(id)})"))
    }
    val dataSourceDisplay = Rx{("", "select a data source") :: selection.dataSources().map(id => (id, id.replace(".json", "")))}

    val experimentSelect = combobox(experimentsDisplay, selection.experiment)
    val dateSelect = combobox(datesDisplay, selection.date)
    val dataSourceSelect = combobox(dataSourceDisplay, selection.dataSource)

    val addChartButton = bs.btnPrimary("Create Chart").render
    addChartButton.onclick = addChart

    val clearButton = bs.btnDefault("Clear").render
    clearButton.onclick = {e: MouseEvent => tags() = List.empty}

    bs.formHorizontal(
      bs.formGroup(bs.col2(label(cls:="control-label", "find data sources")), bs.col10(bs.formInline(bs.formGroup(experimentSelect, dateSelect, dataSourceSelect)))),
      bs.formGroup(bs.col2(label(cls:="control-label", "selected data sources")), bs.col10(tagGroup(tags))),
      bs.formGroup(bs.col12(bs.btnGroup(addChartButton, clearButton)))).render
  }

  def tag(label: String, onclick: MouseEvent => Unit): html.Element = {
    val anchor = a(i(cls:="remove glyphicon glyphicon-remove-sign glyphicon-white")).render
    anchor.onclick = onclick
    div(cls:="row", span(cls:="tag label label-info", label, anchor)).render
  }

}
