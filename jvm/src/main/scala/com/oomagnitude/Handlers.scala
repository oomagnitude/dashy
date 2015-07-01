package com.oomagnitude

import java.nio.file.Path

import scala.concurrent.{ExecutionContextExecutor, Future}

object Handlers {

  def subdirListing(dirPath: Path)(implicit ex: ExecutionContextExecutor): Future[Seq[String]] = {
    Future(dirPath.toFile.listFiles().filterNot(_.isHidden).filter(_.isDirectory)).map {
      case null => Seq.empty[String]
      case files => files.map(_.getName).toSeq
    }
  }

}
