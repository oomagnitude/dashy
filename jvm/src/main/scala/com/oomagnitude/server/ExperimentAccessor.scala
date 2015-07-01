package com.oomagnitude.server

import com.oomagnitude.api.{ExperimentId, ExperimentRunId}

import scala.concurrent.Future

trait ExperimentAccessor {

  def experiments: Future[Seq[ExperimentId]]

  def experimentRuns(id: ExperimentId): Future[Seq[ExperimentRunId]]
}
