package com.oomagnitude.dash.server.filesystem

import com.oomagnitude.api.{ExperimentRunId, ExperimentId}
import com.oomagnitude.dash.server.ExperimentAccessor

import scala.concurrent.{ExecutionContextExecutor, Future}

trait FilesystemExperimentAccessor extends ExperimentAccessor {
  implicit val executionContext: ExecutionContextExecutor

  override def experiments: Future[Seq[ExperimentId]] = {
    filteredListing(ResultsPath) { (file, name) => true }.map {
      case null => Seq.empty[ExperimentId]
      case subdirs => subdirs.filterNot(_.isHidden).filter(_.isDirectory).map(s => ExperimentId(s.getName))
    }
  }

  override def experimentRuns(id: ExperimentId): Future[Seq[ExperimentRunId]] = {
    filteredListing(id.toPath) { (file, name) => true }.map {
      case null => Seq.empty[ExperimentRunId]
      case subdirs => subdirs.filterNot(_.isHidden).filter(_.isDirectory).map(s => ExperimentRunId(id.experiment, s.getName))
    }
  }
}
