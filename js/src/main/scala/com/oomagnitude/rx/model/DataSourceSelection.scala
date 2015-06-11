package com.oomagnitude.rx.model

import com.oomagnitude.api.{DataSourceFetchParams, ExperimentRunId, DataSourceId}
import com.oomagnitude.rx.Buffer
import com.oomagnitude.rx.api.{DataStream, DataPoint, DataSourceApi}
import rx._

object DataSourceSelection {
  def apply(api: DataSourceApi, experimentRunId: Rx[Option[ExperimentRunId]],
            params: Rx[DataSourceFetchParams]): DataSourceSelection = {
    new DataSourceSelection(Var(""), Var(List.empty[DataPoint]), api, experimentRunId, params)
  }
}

class DataSourceSelection(val dataSource: Var[String], val dataPoints: Var[List[DataPoint]], api: DataSourceApi,
                          experimentRunId: Rx[Option[ExperimentRunId]], params: Rx[DataSourceFetchParams]) {
  var stream: Option[DataStream[DataPoint]] = None

  val dataSourceId = Rx {
    experimentRunId().flatMap {
      id =>
        if (dataSource().nonEmpty) Some(DataSourceId(id.experiment, id.date, dataSource()))
        else None
    }
  }

  val dsObs = bind(dataSourceId)
  // TODO: not all config changes should cause a fresh stream (e.g., changing data points per series)
  val paramsObs = bind(params)

  private def bind(observable: Rx[_]): Obs = {
    Obs(observable) {
      // close the current stream, if any
      closeStream()

      dataSourceId().foreach { id =>
        // empty out current buffer
        dataPoints() = List.empty
        // open a new stream of data
        stream = Some(api.fetchDataSource(id, params()))
        Buffer.append(stream.get.data, dataPoints, Some(params().initialBatchSize))
      }
    }
  }

  private def closeStream(): Unit = {
    stream.foreach{_.close()}
    stream = None
  }

}
