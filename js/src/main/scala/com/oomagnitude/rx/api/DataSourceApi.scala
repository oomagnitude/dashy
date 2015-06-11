package com.oomagnitude.rx.api

import com.oomagnitude.Uris
import com.oomagnitude.api.{DataSourceFetchParams, DataSourceId}
import org.scalajs.dom.WebSocket
import rx._
import rx.ops.{DomScheduler, Timer}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait DataSourceApi {
  def fetchDataSource(dataSource: DataSourceId, params: DataSourceFetchParams): DataStream[DataPoint]
}

object RemoteDataSourceApi extends DataSourceApi {
  import Uris._

  override def fetchDataSource(dataSource: DataSourceId, params: DataSourceFetchParams): DataStream[DataPoint] = {
    val url = dataSourceUrl(dataSource, params)
    new WebSocketDataStream(new WebSocket(url), {event => upickle.read[DataPoint](event.data.toString)}, DataPoint.zero)
  }
}

class DummyDataSourceApi(frequency: Option[FiniteDuration]) extends DataSourceApi {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val scheduler = new DomScheduler

  override def fetchDataSource(dataSource: DataSourceId, params: DataSourceFetchParams): DataStream[DataPoint] = {
    val dataPoint = Var(DataPoint.zero)
    if (frequency.nonEmpty) {
      val t = Timer(frequency.get)
      Obs(t) { dataPoint() = update(dataPoint()) }
    } else {
      Future {
        Iterator.from(0).foreach{i => dataPoint() = update(dataPoint())}
      }
    }
    new DummyDataStream(dataPoint)
  }

  private def update(dataPoint: DataPoint): DataPoint = DataPoint(dataPoint.timestep + 1, 100 * math.random)
}