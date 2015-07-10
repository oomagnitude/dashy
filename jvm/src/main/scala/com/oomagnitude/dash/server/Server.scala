package com.oomagnitude.dash.server

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, RequestEntity}
import akka.http.scaladsl.server.Directives
import akka.stream.FlowMaterializer
import com.oomagnitude.api.StreamControl.Resume
import com.oomagnitude.api._
import com.oomagnitude.dash.server.actors.MultiplexFileController
import com.oomagnitude.dash.server.actors.MultiplexFileController.{StreamConfig, StreamSource}
import com.oomagnitude.dash.server.pages.Page
import com.oomagnitude.dash.server.streams.Flows
import com.oomagnitude.metrics.filesystem._
import com.oomagnitude.metrics.model.Metrics._
import com.oomagnitude.metrics.model._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import upickle.{default => upickle}

object Server {
  val JvmRoot = Paths.get(".")
  val JsRoot = JvmRoot.resolve("..").resolve("js").resolve("target").resolve("scala-2.11")
  val JsResources = JsRoot.resolve("classes").resolve("js")
  val ScalaJSOutput = JsRoot
  val CssOutput = JsRoot.resolve("classes").resolve("css")
}

class Server(api: ExperimentApi)(implicit fm: FlowMaterializer, system: ActorSystem,
                                 executor: ExecutionContextExecutor) extends Directives {
  import Flows._
  import Server._

  def route =
    logRequestResult("dashy") {
      get {
      /* START PAGE */
      pathSingleSlash {
        complete {
          HttpEntity(
            MediaTypes.`text/html`,
            "<!DOCTYPE html>\n" + Page.skeleton(Page.ChartBuilderBoot).render
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
        pathPrefix("ws") {
          path("data") {
            parameters('paused.as[Option[Boolean]], 'dataSources.as[String]) {
              (maybePaused: Option[Boolean], dsJson: String) =>
                val paused = maybePaused.getOrElse(false)
                val dataSources = upickle.read[List[DataSourceId]](dsJson)
                val metadatas = Await.result(api.metadata(dataSources), 100.millis)
                val defaultConfig = StreamConfig(frequencyMillis = 100, resolution = 1)
                val flows = metadatas.map { metadata =>
                  val flow = metadata.zero match {
                    case _: Count => parseJson[DataPoint[Count]].via(counterFlow).via(toJs)
                    case _: Time => parseJson[DataPoint[Time]].via(timerFlow).via(toJs)
                    case _: Scalar => parseJson[DataPoint[Scalar]].map(dp => DataPoint(dp.timestep, dp.value.value)).via(toJs)
                    case _ => parseJs
                  }
                  StreamSource(metadata.id, metadata.id.toJsonPath, flow)
                }

                val actor = system.actorOf(MultiplexFileController.props(flows, defaultConfig))
                if (!paused) actor ! Resume
                untypedMessageFlow(actor)
                handleWebsocketMessages(untypedMessageFlow(actor).via(reportErrorsFlow))
            }
          }
        } ~
        getFromResourceDirectory("web")
      } ~
        post {
          path("api" / Segments){ s =>
            extract(ctx => ctx.request.entity) { e: RequestEntity =>
              // TODO: figure out how to use stream Source to accomplish the goal
              complete {
                e match {
                  case HttpEntity.Strict(_, bytes) =>
                    AutowireServer.route[ExperimentApi](api)(autowire.Core.Request(s, upickle.read[Map[String, String]](bytes.utf8String)))
                  case _ =>
                    throw new IllegalArgumentException(s"unexpected HTTP entity type")
                }
              }
            }
          }
        }
    }
}

