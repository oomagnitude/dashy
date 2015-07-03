package com.oomagnitude.dash.server

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives
import akka.stream.FlowMaterializer
import com.oomagnitude.api.StreamControl.Resume
import com.oomagnitude.api.{DataSourceId, ExperimentId, ExperimentRunId}
import com.oomagnitude.dash.server.actors.MultiplexFileController
import com.oomagnitude.dash.server.actors.MultiplexFileController.{StreamConfig, StreamSource}
import com.oomagnitude.dash.server.pages.Page
import com.oomagnitude.dash.server.streams.Flows
import com.oomagnitude.metrics.model._
import com.oomagnitude.metrics.model.ext.MutualInfos

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}

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
          path("data") {
            parameters('paused.as[Option[Boolean]], 'dataSources.as[String]/*, 'dataType.as[Option[String]]*/) {
              (maybePaused: Option[Boolean], dsJson: String /*, maybeDataType: Option[String]*/) =>
                val paused = maybePaused.getOrElse(false)
                val dataType = "Double"//maybeDataType.getOrElse("Double")
                val dataSources = upickle.read[List[DataSourceId]](dsJson)
                val metadatas = Await.result(accessor.metadata(dataSources), 100.millis)
                val defaultConfig = StreamConfig(frequencyMillis = 100, resolution = 1)
                val messageFlow = dataType match {
                  case "Double" =>
                    // only accept those data sources that can be converted to a Double
                    val numericSources = metadatas.filter(_._2.interpretation.isConvertibleTo[Double])
                    val flows = numericSources.map {
                      case (id, metadata) =>
                        val flow = metadata.interpretation match {
                          case Count => Count.transformFlow
                          case Scalar => Scalar.transformFlow
                          case t: Time => t.transformFlow
                          case _ => throw new IllegalArgumentException("non-numeric type detected")
                        }
                        StreamSource(id, id.toJsonPath, flow)
                    }
                    val actor = system.actorOf(MultiplexFileController.props[DataSourceId, DataPoint[Double]](flows, defaultConfig))
                    if (!paused) actor ! Resume
                    Flows.dynamicDataStreamFlow[Double](actor, bufferSize = 100)
                  case "MutualInfos" =>
                    val mutualInfoSources = metadatas.filter(_._2.interpretation.isConvertibleTo[MutualInfos])
                    val flows = mutualInfoSources.map {
                      case (id, metadata) =>
                        StreamSource(id, id.toJsonPath, Flows.parseJson[DataPoint[MutualInfos]])
                    }
                    val actor = system.actorOf(MultiplexFileController.props[DataSourceId, DataPoint[MutualInfos]](flows, defaultConfig))
                    if (!paused) actor ! Resume
                    Flows.dynamicDataStreamFlow[MutualInfos](actor, bufferSize = 100)
                }
                handleWebsocketMessages(messageFlow.via(reportErrorsFlow))
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

