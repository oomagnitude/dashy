package com.oomagnitude.rx.view

import com.oomagnitude.rx.model.ExperimentSelection
import combobox.Combobox
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.jquery._
import rx._

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

object Templates {
  import com.oomagnitude.rx.Rxs._
  
  object Dynamic {
    def combobox(options: Rx[List[String]], selected: Var[String]): html.Select = {
      val optionFragments = Rx {("" :: options()).map(opt => option(value:=opt, opt))}

      val selectTag: html.Select = select(cls:="combobox form-control",
        optionFragments.asFrags({ parent =>
          if (!options().contains(selected())) {
            // Reset the selected item in this list
            selected() = ""
          }

          Combobox.refresh(jQuery(parent))
        })
      ).render

      selectTag.onchange = { e: dom.Event => selected() = selectTag.value }

      selectTag
    }

    def ulist(items: Rx[List[String]]): html.UList = ul(toListItems(items).asFrags()).render

    def olist(items: Rx[List[String]]): html.OList = ol(toListItems(items).asFrags()).render

    def toListItems(items: Rx[List[String]]): Rx[List[TypedTag[html.LI]]] = Rx {
      // TODO: this hack exists because Rxs.RxListOps.asFrags requires the list to be nonempty
      if (items().isEmpty) List(li(""))
      else items().map(li(_))
    }
  }

  def experimentForm(selection: ExperimentSelection, dataSource: Var[String]): html.Form = {
    val experimentSelect = Dynamic.combobox(selection.experiments, selection.experiment)
    val dateSelect = Dynamic.combobox(selection.dates, selection.date)
    val dataSourceSelect = Dynamic.combobox(selection.dataSources, dataSource)

    form(role:="form",div(cls:="form-group", experimentSelect, dateSelect, dataSourceSelect)).render
  }

}
