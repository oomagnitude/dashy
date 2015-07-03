package com.oomagnitude

import com.oomagnitude.metrics.model.DataPoint

package object api {
  type DataPoints[T] = Iterable[(DataSourceId, DataPoint[T])]
  type JsValues = Iterable[(DataSourceId, upickle.Js.Value)]
}
