package com.oomagnitude.rx.model

import com.oomagnitude.api.{ExperimentId, ExperimentRunId}
import com.oomagnitude.rx.api.ExperimentApi
import rx._

object ExperimentSelection {
  def apply(api: ExperimentApi) = new ExperimentSelection(api)
}

class ExperimentSelection(api: ExperimentApi) {
  import scala.concurrent.ExecutionContext.Implicits.global
  private[this] val es = Var(List.empty[String])
  private[this] val ds = Var(List.empty[String])
  private[this] val dss = Var(List.empty[String])

  val dates: Rx[List[String]] = ds
  val experiments: Rx[List[String]] = es
  val dataSources: Rx[List[String]] = dss

  val date = Var("")
  val experiment = Var("")

  val experimentId = Rx {
    if (experiment().nonEmpty) Some(ExperimentId(experiment()))
    else None
  }

  val experimentRunId = Rx {
    experimentId().flatMap {
      id =>
        if (date().nonEmpty) Some(ExperimentRunId(id.experiment, date()))
        else None
    }
  }

  // fetch experiments immediately
  api.experiments().onSuccess {case e => es() = e}

  var datesUpdates = 0
  Obs(experimentId) {
    datesUpdates += 1; val c = datesUpdates
    ds() = List.empty

    experimentId().foreach {
      // TODO: how to handle failure?
      api.dates(_).onSuccess { case d =>
        // Only update if this is the latest change
        if (c == datesUpdates) ds() = d
      }
    }
  }

  var sourcesUpdates = 0
  Obs(experimentRunId) {
    sourcesUpdates += 1; val c = sourcesUpdates
    dss() = List.empty

    experimentRunId().foreach {
      api.dataSources(_).onSuccess { case s =>
        // Only update if this is the latest change
        if (c == datesUpdates) dss() = s
      }
    }
  }
}
