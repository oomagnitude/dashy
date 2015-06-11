package com.oomagnitude.rx.api

object DataPoint {
  val zero = DataPoint(0,0)
}

/**
 * NOTE: timestep is an Int and not a Long because JavaScript does not support the full range of Long values. See
 * this post for more info:
 *
 * https://github.com/lihaoyi/upickle/issues/66
 *
 * @param timestep
 * @param value
 */
case class DataPoint(timestep: Int, value: Double)