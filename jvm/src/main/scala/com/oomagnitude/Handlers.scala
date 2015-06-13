package com.oomagnitude

import java.nio.file.Path

import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl._
import com.oomagnitude.api.DataSourceFetchParams
import com.oomagnitude.streams.Flows

import scala.concurrent.{ExecutionContextExecutor, Future}

object Handlers {
  import Flows._

  def dirListing(dirPath: Path)(implicit ex: ExecutionContextExecutor): Future[Seq[String]] = {
    Future(dirPath.toFile.listFiles().filterNot(_.isHidden)).map {
      case null => Seq.empty[String]
      case files => files.map(_.getName).toSeq
    }
  }

//  def streamFile(path: Path, params: DataSourceFetchParams): Flow[Message, Message, Any] = {
//    // flow that grafts the file source into websocket messages sent to the client
//    serverToClientMessageFlow(fileSource(path, params))
//  }

}
