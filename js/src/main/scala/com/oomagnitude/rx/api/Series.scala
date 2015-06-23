package com.oomagnitude.rx.api

import com.oomagnitude.api.DataPoint

case class Series(key: String, values: Seq[DataPoint[Double]])
