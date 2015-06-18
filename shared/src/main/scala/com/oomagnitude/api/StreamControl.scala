package com.oomagnitude.api

object StreamControl {
  sealed trait MetricMode
  sealed trait StreamControlMessage
  case object Sample extends StreamControlMessage with MetricMode
  case object Rate extends StreamControlMessage with MetricMode
  case object Pause extends StreamControlMessage
  case object Resume extends StreamControlMessage
  case class Seek(timestep: Int) extends StreamControlMessage
  case class Resolution(timestepInterval: Int) extends StreamControlMessage
  case class Frequency(millis: Int) extends StreamControlMessage
}
