package com.oomagnitude.model

import com.oomagnitude.api.ExperimentApi
import rx._

class ChartBuilderData {
  val experimentSelection = new ExperimentSelection
  val title: Var[Option[String]] = Var(None)
  val dataSources = new RxOptionList(experimentSelection.dataSourceId)

  def clear(): Unit = {
    dataSources.clear()
    experimentSelection.clear()
    title() = None
  }
}
