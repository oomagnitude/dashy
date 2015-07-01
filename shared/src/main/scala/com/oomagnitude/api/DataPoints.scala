package com.oomagnitude.api

import com.oomagnitude.metrics.model.DataPoint

case class DataPoints[T](map: Iterable[(DataSourceId, DataPoint[T])])
