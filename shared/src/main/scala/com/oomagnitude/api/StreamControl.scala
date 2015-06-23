package com.oomagnitude.api

object StreamControl {
  sealed trait StreamControlMessage
  sealed trait MetricMode extends StreamControlMessage

  /**
   * Send the next data point to the client. Useful to implement pull mode and/or get a data point before the configured
   * frequency
   */
  case object Next extends StreamControlMessage

  /**
   * Sample data points from the data source (don't perform any calculations on them)
   */
  case object Sample extends MetricMode

  /**
   * Compute a rate of change from data source per unit time
   */
  case object Rate extends MetricMode

  /**
   * Stop sending data points until [[Resume]] is received
   */
  case object Pause extends StreamControlMessage

  /**
   * Resume sending data points until [[Pause]] is received
   */
  case object Resume extends StreamControlMessage

  /**
   * Alias for Resume (for streams that are initially paused)
   */
  val Start = Resume

  /**
   * Seek to the first data point with a timestep at the requested value, or later
   * @param timestep the timestep to seek to
   */
  case class Seek(timestep: Int) extends StreamControlMessage

  /**
   * Change the resolution (i.e., sample frequency) for data points . Default value is 1.
   * @param timestepInterval the interval between data points, in timesteps
   */
  case class Resolution(timestepInterval: Int) extends StreamControlMessage

  /**
   * Set the frequency of sending new data points across the wire. Default value is 100.
   * @param millis the frequency at which data points will be sent to the client. A value of 200 means that
   *               approximately every 200ms, a new data point will be sent to the client
   */
  case class Frequency(millis: Int) extends StreamControlMessage
}
