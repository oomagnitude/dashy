package com.oomagnitude.js.service

import biz.enef.angulate.Service

import scala.scalajs.js

class DisplaySettingService() extends Service {
  val displaySettings = js.Dynamic.literal(
    maxDataPointsPerSeries = 100,
    dataPointFrequencySeconds = 1,
    timestepResolution = 10)
}
