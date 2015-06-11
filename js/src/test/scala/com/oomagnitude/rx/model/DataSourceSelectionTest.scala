package com.oomagnitude.rx.model

import com.oomagnitude.api.ExperimentRunId
import com.oomagnitude.rx.Client
import com.oomagnitude.rx.api.DummyDataSourceApi
import rx._
import utest._

import scala.util.{Failure, Success}

object DataSourceSelectionTest extends TestSuite {
  import JsOps._

  import scala.concurrent.duration._
  implicit val timeout = 30.milliseconds

  val tests = TestSuite {
    'getsUpdated {
      val experimentRunId = Var[Option[ExperimentRunId]](None)
      val params = Var(Client.DefaultFetchParams)
        val selection = DataSourceSelection(new DummyDataSourceApi(frequency = Some(10.milliseconds)),
          experimentRunId, params)

      // data stream is triggered by experiment run ID and data source being set
      experimentRunId() = Some(ExperimentRunId("experiment", "date"))
      selection.dataSource() = "dummy"

      eventually {
        if (selection.dataPoints().size > 1) {
          Success(true)
        } else {
          Failure(new IllegalStateException(s"data points did not exceed size 1 ${selection.dataPoints()}"))
        }
      }
    }
  }

}
