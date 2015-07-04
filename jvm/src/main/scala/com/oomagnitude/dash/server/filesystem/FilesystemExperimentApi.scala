package com.oomagnitude.dash.server.filesystem

import java.io.{FilenameFilter, File}
import java.nio.file.Path

import com.oomagnitude.api.ExperimentApi
import com.oomagnitude.metrics.model.{DataSourceId, ExperimentId, MetricMetadata, ExperimentRunId}
import com.oomagnitude.metrics.filesystem._

import scala.concurrent.{ExecutionContextExecutor, Future}

class FilesystemExperimentApi(implicit executionContext: ExecutionContextExecutor) extends ExperimentApi {
  override def experiments(): Future[List[ExperimentId]] = {
    filteredListing(ResultsPath) { (file, name) => true }.map {
      case null => List.empty[ExperimentId]
      case subdirs => subdirs.filterNot(_.isHidden).filter(_.isDirectory).map(s => ExperimentId(s.getName))
    }
  }

  override def experimentRuns(id: ExperimentId): Future[List[ExperimentRunId]] = {
    filteredListing(id.toPath) { (file, name) => true }.map {
      case null => List.empty[ExperimentRunId]
      case subdirs => subdirs.filterNot(_.isHidden).filter(_.isDirectory).map(s => ExperimentRunId(id, s.getName))
    }
  }

  override def dataSources(id: ExperimentRunId): Future[List[MetricMetadata]] = {
    val listing = filteredListing(id.metricsPath) {(dir, name) => name.toLowerCase.endsWith(".meta")}
    listing.flatMap(getAllContents).map(_.map(json => upickle.read[MetricMetadata](json)))
  }

  override def metadata(dataSources: List[DataSourceId]): Future[List[MetricMetadata]] = {
    Future.sequence(dataSources.map { id =>
      Future {
        val source = io.Source.fromFile(id.toMetaPath.toFile)
        try upickle.read[MetricMetadata](source.getLines().mkString) finally source.close()
      }
    })
  }

  // TODO: async I/O instead of using a future here?
  def filteredListing(path: Path)(filter: (File, String) => Boolean): Future[List[File]] = {
    Future(path.toFile.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = filter(dir, name)
    }).toList)
  }

  def getAllContents(listing: Iterable[File]): Future[List[String]] = {
    listing match {
      case null => Future.successful(List.empty[String])
      case files =>
        Future.sequence(files.toList.map { file =>
          Future {
            val source = io.Source.fromFile(file)
            try source.getLines().mkString finally source.close()
          }
        })
    }
  }
}
