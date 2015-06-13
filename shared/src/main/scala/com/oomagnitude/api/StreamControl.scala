package com.oomagnitude.api

object StreamControl {
  case object Pause extends StreamControlMessage
  case object Resume extends StreamControlMessage
  case class Seek(timestep: Int) extends StreamControlMessage
  case class Resolution(timestepInterval: Int) extends StreamControlMessage
  case class Frequency(millis: Int) extends StreamControlMessage
  sealed trait StreamControlMessage
}
