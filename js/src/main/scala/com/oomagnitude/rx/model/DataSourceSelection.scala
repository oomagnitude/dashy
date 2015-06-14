package com.oomagnitude.rx.model

import com.oomagnitude.api.DataSourceId
import rx._

class DataSourceSelection(dataSourceId: Rx[Option[DataSourceId]]) {
  val selectedSources = Var(List.empty[DataSourceId])

  Obs(dataSourceId) {
    dataSourceId().foreach { id =>
      selectedSources() = id :: selectedSources()
    }
  }

}
