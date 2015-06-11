package com.oomagnitude.rx.model

import com.oomagnitude.api.{ExperimentId, ExperimentRunId}
import com.oomagnitude.rx.api.DummyExperimentApi
import rx.ops.DomScheduler
import utest._

import scala.util.{Failure, Success}

object ExperimentSelectionTest extends TestSuite {
  import JsOps._
  import scala.concurrent.duration._

  val scheduler = new DomScheduler
  implicit val timeout = 100.milliseconds
  val experiments = List("exp0", "exp1", "exp2", "exp3", "exp4", "exp5", "exp6", "exp7", "exp8", "exp9")
  val dates = List("day0", "day1", "day2", "day3", "day4", "day5", "day6", "day7", "day8", "day9")
  val dataSources = List("ds0", "ds1", "ds2", "ds3", "ds4", "ds5", "ds6", "ds7", "ds8", "ds9")
  val api = new DummyExperimentApi(experiments, dates, dataSources)

  val tests = TestSuite {
    'experimentIdEmpty {
      val selection = ExperimentSelection(api)
      assert(selection.experimentId().isEmpty)

      selection.experiment() = "exp"
      assert(selection.experimentId().contains(ExperimentId("exp")))

      selection.experiment() = ""
      assert(selection.experimentId().isEmpty)
    }

    'experimentRunIdEmpty {
      val selection = ExperimentSelection(api)
      assert(selection.experimentRunId().isEmpty)

      selection.experiment() = "exp"
      assert(selection.experimentRunId().isEmpty)

      selection.date() = "date"
      assert(selection.experimentRunId().contains(ExperimentRunId("exp", "date")))

      selection.experiment() = ""
      assert(selection.experimentRunId().isEmpty)
    }

    'datesBecomeNonEmpty {
      val selection = ExperimentSelection(api)
      assert(selection.dates().isEmpty)
      assert(selection.dataSources().isEmpty)

      selection.experiment() = "exp"
      selection.date() = "date"

      eventually {
        if (selection.experiments().nonEmpty && selection.dates().nonEmpty && selection.dataSources().nonEmpty)
          Success(true)
        else Failure(new IllegalStateException(""))
      }
    }

    'datesEmptyAfterExperimentCleared {
      val selection = ExperimentSelection(api)
      // set up experiment and date, which should cause the lists to be populated
      selection.experiment() = "exp"
      selection.date() = "date"
      assert(selection.experimentRunId().contains(ExperimentRunId("exp", "date")))

      // clear the experiment which could cause all lists but the experiments list to be cleared
      selection.experiment() = ""
      assert(selection.experimentId().isEmpty)
      assert(selection.experimentRunId().isEmpty)

      eventually {
        if (selection.experiments().nonEmpty && selection.dates().isEmpty && selection.dataSources().isEmpty) {
          Success(true)
        } else Failure(new IllegalStateException(s"lists not as expected (experiments: ${selection.experiments().size}" +
          s", dates: ${selection.dates().size}, dataSources: ${selection.dataSources().size})"))
      }
    }
  }
}
