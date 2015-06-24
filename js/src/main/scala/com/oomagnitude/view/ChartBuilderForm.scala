package com.oomagnitude.view

import com.oomagnitude.model.ChartBuilderData
import com.oomagnitude.view.Typeahead.SelectOption
import org.scalajs.dom.html
import org.scalajs.dom.raw.MouseEvent
import rx._

import scalatags.JsDom.all._

object ChartBuilderForm {
  import Templates._

  def apply(bd: ChartBuilderData, addChart: MouseEvent => Unit): html.Element = {
    val experimentsDisplay = Rx {bd.experimentSelection.experiments().sorted.map(id => SelectOption(id, id))}
    val datesDisplay = Rx {bd.experimentSelection.dates().sorted(Ordering.String.reverse).map(id =>
        SelectOption(s"${moment.humanReadableDurationFromNow(id)} (${moment.calendarDate(id)})", id))
    }
    val dataSourceDisplay = Rx{bd.experimentSelection.dataSources().map(id => SelectOption(id.replace(".json", ""), id))}

    val experimentSelect = typeahead("select an experiment", experimentsDisplay, bd.experimentSelection.experiment)
    val dateSelect = typeahead("select a date", datesDisplay, bd.experimentSelection.date)
    val dataSourceSelect = typeahead("select a data source", dataSourceDisplay, bd.experimentSelection.dataSource)

    val addChartButton = bs.btnPrimary(onclick:= addChart, "Create Chart").render

    val clearButton = bs.btnDefault("Clear", onclick:= {e: MouseEvent => bd.clear()}).render

    bs.formHorizontal(
      bs.formGroup(bs.col2(label(cls:="control-label", "find data sources")),
        bs.col10(bs.formInline(bs.formGroup(bs.col4(experimentSelect), bs.col4(dateSelect), bs.col4(dataSourceSelect))))),
      bs.formGroup(bs.col2(label(cls:="control-label", "selected data sources")), bs.col10(bd.dataSourceTags.element)),
      bs.formGroup(bs.col2(label(cls:="control-label", "chart title")), bs.col10(textInput(bd.title))),
      bs.formGroup(bs.col12(bs.btnGroup(addChartButton, clearButton)))).render
  }
}
