package com.oomagnitude

import java.nio.file.{Path, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives
import akka.stream.FlowMaterializer
import com.oomagnitude.actors.FileStreamActor
import com.oomagnitude.api.{DataSourceId, ExperimentId, ExperimentRunId}
import com.oomagnitude.metrics.model.ext.MutualInfos
import com.oomagnitude.pages.Page
import com.oomagnitude.server.Accessor
import com.oomagnitude.streams.Flows

import scala.concurrent.ExecutionContextExecutor

object Server {
  val JvmRoot = Paths.get(".")
  val JsRoot = JvmRoot.resolve("..").resolve("js").resolve("target").resolve("scala-2.11")
  val JsResources = JsRoot.resolve("classes").resolve("js")
  val ScalaJSOutput = JsRoot
  val CssOutput = JsRoot.resolve("classes").resolve("css")
}

class Server(accessor: Accessor)(implicit fm: FlowMaterializer, system: ActorSystem, executor: ExecutionContextExecutor) extends Directives {
  import Flows._
  import Server._
  import filesystem._

  def route =
    logRequestResult("scalajs-dashboard") {
      get {
      /* START PAGE */
      pathSingleSlash {
        complete{
          HttpEntity(
            MediaTypes.`text/html`,
            "<!DOCTYPE html>\n" + Page.Skeleton.render
          )
        }
      } ~
        // Scala-JS puts them in the root of the resource directory per default,
        // so that's where we pick them up
        pathPrefix("js") {
          path(Segment) { filename =>
            val outputFile = ScalaJSOutput.resolve(filename).toFile
            if (outputFile.exists()) {
              getFromFile(outputFile)
            } else {
              getFromFile(JsResources.resolve(filename).toFile)
            }
          }
        } ~
        pathPrefix("css") {
          path(Segment) { filename =>
            getFromFile(CssOutput.resolve(filename).toFile)
          }
        } ~
        pathPrefix("api") {
          path("data" / Segment / Segment / Segment) {(experiment, date, dataSource) =>
            parameters('paused.as[Option[Boolean]]) { maybePaused =>
              val paused = maybePaused.getOrElse(false)
              val path: Path = DataSourceId(experiment, date, dataSource).toJsonPath
              if (dataSource == "mutualInformation") {
                val actor = system.actorOf(FileStreamActor.props[MutualInfos](path, paused))
                handleWebsocketMessages(Flows.dynamicDataStreamFlow[MutualInfos](actor, bufferSize = 100).via(reportErrorsFlow))
              } else {
                val actor = system.actorOf(FileStreamActor.props[Double](path, paused))
                handleWebsocketMessages(Flows.dynamicDataStreamFlow[Double](actor, bufferSize = 100).via(reportErrorsFlow))
              }
            }
          } ~
          path("experiments" / Segment / Segment) { (experiment, date) =>
            complete {
              accessor.metadata(ExperimentRunId(experiment, date)).map(m => upickle.write(m))
            }
          } ~
          path("experiments" / Segment) { experiment =>
            complete {
              // TODO: change API to deal with ID case classes directly
              accessor.experimentRuns(ExperimentId(experiment)).map { ids =>
                upickle.write(ids.map(_.date))
              }
            }
          } ~
          path("experiments") {
            complete {
              accessor.experiments.map { ids =>
                upickle.write(ids.map(_.experiment))
              }
            }
          }
        } ~
        getFromResourceDirectory("web")
    }
    }
}

