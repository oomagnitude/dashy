package com.oomagnitude

import java.nio.file.Path

import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl._
import com.oomagnitude.streams.Flows

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps

object Handlers {
  import Flows._

  def dirListing(dirPath: Path)(implicit ex: ExecutionContextExecutor): Future[Seq[String]] = {
    Future(dirPath.toFile.listFiles().filterNot(_.isHidden)).map {
      case null => Seq.empty[String]
      case files => files.map(_.getName).toSeq
    }
  }

  def streamFile(path: Path, params: FetchParams): Flow[Message, Message, Any] = {
    // source that emits one string for every line in the file
    val fileSource = lineByLineFile(path)

    // source that emits every data point (message) that will be sent to the client
    val everyDataPoint =
      if (params.timestepResolution.nonEmpty) {
        val timestepResolution = params.timestepResolution.get
        fileSource.via(everyN(timestepResolution))
      } else fileSource

    // source that will emit a data point no more often than requested by the client
    val throttledSource: Source[String, _] = if (params.dataPointFrequencySeconds.nonEmpty) {
        val dataPointFrequency = (params.dataPointFrequencySeconds.get seconds)
        everyDataPoint
          // immediately return all data points up to the initial batch size
          .take(params.initialBatchSize)
          // throttle all subsequent data points
          .concat(
            everyDataPoint
              .drop(params.initialBatchSize)
              .via(throttled(delay = 0 seconds, interval = dataPointFrequency)))
      } else everyDataPoint

    // flow that grafts the file source into websocket messages sent to the client
    serverToClientMessageFlow(throttledSource)
  }


}
