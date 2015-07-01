package com.oomagnitude.filesystem

import com.oomagnitude.api.{DataSourceId, ExperimentRunId}
import com.oomagnitude.metrics.model.MetricMetadata
import com.oomagnitude.server.MetadataAccessor

import scala.concurrent.{ExecutionContextExecutor, Future}

trait FilesystemMetadataAccessor extends MetadataAccessor {
  implicit val executionContext: ExecutionContextExecutor

  override def metadata(experimentRun: ExperimentRunId): Future[Seq[MetricMetadata]] = {
    val listing = filteredListing(experimentRun.toPath) {(dir, name) => name.toLowerCase.endsWith(".meta")}
    listing.flatMap(getAllContents).map(_.map(json => upickle.read[MetricMetadata](json)))
  }

  override def metadata(dataSources: Iterable[DataSourceId]): Future[Seq[MetricMetadata]] = {
    val files = dataSources.map(_.toMetaPath.toFile)
    getAllContents(files).map(_.map(json => upickle.read[MetricMetadata](json)))
  }
}