package com.oomagnitude.dash.server.filesystem

import com.oomagnitude.api.{DataSourceId, ExperimentRunId}
import com.oomagnitude.dash.server.MetadataAccessor
import com.oomagnitude.metrics.model.MetricMetadata

import scala.concurrent.{ExecutionContextExecutor, Future}

trait FilesystemMetadataAccessor extends MetadataAccessor {
  implicit val executionContext: ExecutionContextExecutor

  override def metadata(experimentRun: ExperimentRunId): Future[Seq[MetricMetadata]] = {
    val listing = filteredListing(experimentRun.toPath) {(dir, name) => name.toLowerCase.endsWith(".meta")}
    listing.flatMap(getAllContents).map(_.map(json => upickle.read[MetricMetadata](json)))
  }

  override def metadata(dataSources: Iterable[DataSourceId]): Future[Seq[(DataSourceId, MetricMetadata)]] = {
    Future.sequence(dataSources.toSeq.map { id =>
      Future {
        val source = io.Source.fromFile(id.toMetaPath.toFile)
        (id, try upickle.read[MetricMetadata](source.getLines().mkString) finally source.close())
      }
    })
  }
}