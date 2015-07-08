package com.oomagnitude.view

import com.oomagnitude.bind.ModelViewBinding
import com.oomagnitude.metrics.model.Metrics.MetricMetadata
import com.oomagnitude.model.ChartFormData
import org.scalajs.dom.html
import org.scalajs.dom.raw.MouseEvent

import scalatags.JsDom.all._

object ChartBuilderForm {
  import Templates._

  def apply(bd: ChartFormData, addChart: MouseEvent => Unit): html.Element = {
    val experimentSelect = typeahead("select an experiment", bd.experimentOptions)
    val dateSelect = typeahead("select a date", bd.experimentRunOptions)
    val dataSourceSelect = typeahead("select a data source", bd.dataSourceOptions)

    val addChartButton = bs.btnPrimary(onclick:= addChart, "Create Chart").render

    val clearButton = bs.btnDefault("Clear", onclick:= {e: MouseEvent => bd.clear()}).render
    val dataSourceTags = new RxElementGroup
    val dsBinding = new ModelViewBinding(bd.selectedDataSources, dataSourceTags)({
      (m: MetricMetadata, remove: () => Unit) => Templates.tag(m.id.toString, {e => remove()})
    })

    bs.formHorizontal(
      bs.formGroup(bs.col2(label(cls:="control-label", "find data sources")),
        bs.col10(bs.formInline(bs.formGroup(bs.col4(experimentSelect), bs.col4(dateSelect), bs.col4(dataSourceSelect))))),
      bs.formGroup(bs.col2(label(cls:="control-label", "selected data sources")), bs.col10(dsBinding.element)),
      bs.formGroup(bs.col2(label(cls:="control-label", "chart title")), bs.col10(textInput(bd.title))),
      bs.formGroup(bs.col12(bs.btnGroup(addChartButton, clearButton)))).render
  }
}
