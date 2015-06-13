package com.oomagnitude.angular.service

import biz.enef.angulate.Service
import com.oomagnitude.api.DataSourceFetchParams

import scala.scalajs.js

class DisplaySettingService() extends Service {
  val displaySettings = js.Dynamic.literal(
    maxDataPointsPerSeries = 100,
    dataPointFrequencySeconds = 1,
    timestepResolution = 10)

  def fetchParams: DataSourceFetchParams = {
    DataSourceFetchParams(displaySettings.maxDataPointsPerSeries.asInstanceOf[Int],
      displaySettings.timestepResolution.asInstanceOf[Int],
      Some(displaySettings.dataPointFrequencySeconds.asInstanceOf[Int]), None)
  }
}
