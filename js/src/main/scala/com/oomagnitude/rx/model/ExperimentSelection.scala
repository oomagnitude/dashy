package com.oomagnitude.rx.model

import com.oomagnitude.api.{DataSourceId, ExperimentId, ExperimentRunId}
import com.oomagnitude.rx.api.ExperimentApi
import rx._

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
  val dataSource = Var("")

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

  val dataSourceId = Rx {
    experimentRunId().flatMap {
      id =>
        if (dataSource().nonEmpty) Some(DataSourceId(id.experiment, id.date, dataSource()))
        else None
    }
  }

  // fetch experiments immediately
  api.experiments().onSuccess {case e => es() = e}

  private[this] val datesId = new Counter
  Obs(experimentId) {
    val c = datesId.increment()
    ds() = List.empty

    experimentId().foreach {
      // TODO: how to handle failure?
      api.dates(_).onSuccess { case d =>
        // Only update if this is the latest change
        if (datesId.isCurrent(c)) ds() = d
      }
    }
  }

  private[this] val sourcesId = new Counter
  Obs(experimentRunId) {
    val c = sourcesId.increment()
    dss() = List.empty

    experimentRunId().foreach {
      api.dataSources(_).onSuccess { case s =>
        // Only update if this is the latest change
        if (sourcesId.isCurrent(c)) dss() = s
      }
    }
  }

  private class Counter {
    private[this] var counter = 0
    def increment(): Int = {
      counter += 1; counter
    }
    def isCurrent(value: Int) = value == counter

  }
}
