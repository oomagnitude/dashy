package com.oomagnitude.dash.server

import com.oomagnitude.metrics.model.{ExperimentRunId, ExperimentId}

import scala.concurrent.Future

trait ExperimentAccessor {

  def experiments: Future[Seq[ExperimentId]]

  def experimentRuns(id: ExperimentId): Future[Seq[ExperimentRunId]]
}
