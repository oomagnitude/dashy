package com.oomagnitude.rx.api

import com.oomagnitude.api.DataPoint

import scala.scalajs.js
import scala.scalajs.js.JSON

object Series {
  implicit class SeriesListOps(series: List[Series]) {
    def asJs: js.Dynamic = JSON.parse(upickle.write(series))
  }
}
case class Series(key: String, values: List[DataPoint])
