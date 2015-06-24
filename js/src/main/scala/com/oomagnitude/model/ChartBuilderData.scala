package com.oomagnitude.model

import com.oomagnitude.api.DataSourceId
import com.oomagnitude.rx.api.ExperimentApi
import com.oomagnitude.view.{RxOptionElementGroup, Templates}
import rx._

class ChartBuilderData(api: ExperimentApi) {
  val experimentSelection = new ExperimentSelection(api)
  val title: Var[Option[String]] = Var(None)
  val dataSourceTags = new RxOptionElementGroup[DataSourceId](experimentSelection.dataSourceId, {
    (dataSourceId, remove) => Templates.tag(dataSourceId.toString, {e => remove()})
  })

  def clear(): Unit = {
    dataSourceTags.clear()
    experimentSelection.clear()
    title() = None
  }
}
