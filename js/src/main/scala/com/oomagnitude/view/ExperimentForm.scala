package com.oomagnitude.view

import com.oomagnitude.rx.model.ExperimentSelection
import org.scalajs.dom.html
import org.scalajs.dom.raw.MouseEvent
import rx._

import scalatags.JsDom.all._

object ExperimentForm {
  import Templates._

  def apply(selection: ExperimentSelection, tagGroup: RxOptionElementGroup[_], title: Var[Option[String]],
            addChart: MouseEvent => Unit): html.Element = {
    val experimentsDisplay = Rx {("", "select an experiment") :: selection.experiments().sorted.map(id => (id, id))}
    val datesDisplay = Rx {
      ("", "select a date") :: selection.dates().sorted(Ordering.String.reverse).map(id =>
        (id, s"${moment.humanReadableDurationFromNow(id)} (${moment.calendarDate(id)})"))
    }
    val dataSourceDisplay = Rx{("", "select a data source") ::
      selection.dataSources().map(id => (id, id.replace(".json", "")))}

    val experimentSelect = combobox(experimentsDisplay, selection.experiment)
    val dateSelect = combobox(datesDisplay, selection.date)
    val dataSourceSelect = combobox(dataSourceDisplay, selection.dataSource)

    val addChartButton = bs.btnPrimary(onclick:= addChart, "Create Chart").render
    val clearButton = bs.btnDefault("Clear", onclick:= {e: MouseEvent => tagGroup.clear()}).render

    bs.formHorizontal(
      bs.formGroup(bs.col2(label(cls:="control-label", "find data sources")),
        bs.col10(bs.formInline(bs.formGroup(experimentSelect, dateSelect, dataSourceSelect)))),
      bs.formGroup(bs.col2(label(cls:="control-label", "selected data sources")), bs.col10(tagGroup.element)),
      bs.formGroup(bs.col2(label(cls:="control-label", "chart title")), bs.col10(textInput(title))),
      bs.formGroup(bs.col12(bs.btnGroup(addChartButton, clearButton)))).render
  }
}
