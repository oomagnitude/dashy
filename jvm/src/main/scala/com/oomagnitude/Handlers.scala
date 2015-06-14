package com.oomagnitude

import java.nio.file.Path

import scala.concurrent.{ExecutionContextExecutor, Future}

object Handlers {

  def dirListing(dirPath: Path)(implicit ex: ExecutionContextExecutor): Future[Seq[String]] = {
    Future(dirPath.toFile.listFiles().filterNot(_.isHidden)).map {
      case null => Seq.empty[String]
      case files => files.map(_.getName).toSeq
    }
  }

}
