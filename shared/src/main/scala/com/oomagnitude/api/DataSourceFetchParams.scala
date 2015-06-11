package com.oomagnitude.api

/**
 * Parameters that modulate behavior of data source fetch
 *
 * @param initialBatchSize number of data points to send immediately
 * @param resolution how many data points to drop from the original source in between each data point sent to
 *                   the client. The higher the number, the coarser the resolution. Assumes data points are spaced
 *                   evenly apart.
 * @param frequencySeconds how many seconds apart to send new data points
 */
case class DataSourceFetchParams(initialBatchSize: Int, resolution: Option[Int], frequencySeconds: Option[Long])