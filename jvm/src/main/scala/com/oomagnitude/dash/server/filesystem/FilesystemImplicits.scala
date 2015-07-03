package com.oomagnitude.dash.server.filesystem

import java.io.{FilenameFilter, File}
import java.nio.file.{Path, Paths}

import com.oomagnitude.api.{DataSourceId, ExperimentRunId, ExperimentId}

import scala.concurrent.{Future, ExecutionContextExecutor}

trait FilesystemImplicits {
  def pathForProperty(propertyName: String, default: String = "."): String = {
    val path =
      sys.env.getOrElse(propertyName, sys.props.getOrElse(propertyName, default))
    new File(path).getAbsolutePath
  }

  val OutputDataPath = Paths.get(pathForProperty("CLA_EXP"))
  val ResultsPath = OutputDataPath.resolve("results")

  implicit class ExperimentIdExt(id: ExperimentId) {
    def toPath: Path = ResultsPath.resolve(id.experiment)
  }

  implicit class ExperimentRunIdExt(id: ExperimentRunId) {
    def toPath: Path = ExperimentId(id.experiment).toPath.resolve(id.date).resolve("json")
  }

  implicit class DataSourceIdExt(id: DataSourceId) {
    private def basePath: Path = ExperimentRunId(id.experiment, id.date).toPath
    def toJsonPath: Path = basePath.resolve(id.name + ".json")
    def toMetaPath: Path = basePath.resolve(id.name + ".meta")
  }

  // TODO: async I/O instead of using a future here?
  def filteredListing(path: Path)(filter: (File, String) => Boolean)(implicit ex: ExecutionContextExecutor): Future[Seq[File]] = {
    Future(path.toFile.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = filter(dir, name)
    }).toSeq)
  }

  def getAllContents(listing: Iterable[File])(implicit ex: ExecutionContextExecutor): Future[Seq[String]] = {
    listing match {
      case null => Future.successful(Seq.empty[String])
      case files =>
        Future.sequence(files.toList.map { file =>
          Future {
            val source = io.Source.fromFile(file)
            try source.getLines().mkString finally source.close()
          }
        })
    }
  }
}
