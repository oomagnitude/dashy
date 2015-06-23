package com.oomagnitude

import java.io.File
import java.nio.file.{Path, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.stage.{Context, PushStage, SyncDirective, TerminationDirective}
import com.oomagnitude.api.MutualInfos
import com.oomagnitude.pages.Page
import com.oomagnitude.streams.{FileStreamActor, Flows}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.ExecutionContextExecutor

object Server {
  def pathForProperty(propertyName: String, default: String = "."): String = {
    val path =
      sys.env.getOrElse(propertyName, sys.props.getOrElse(propertyName, default))
    new File(path).getAbsolutePath
  }

  val OutputDataPath = Paths.get(pathForProperty("CLA_EXP"))

  val ResultsPath = OutputDataPath.resolve("results")

  val JvmRoot = Paths.get(".")
  val JsRoot = JvmRoot.resolve("..").resolve("js").resolve("target").resolve("scala-2.11")
  val JsResources = JsRoot.resolve("classes").resolve("js")
  val ScalaJSOutput = JsRoot
  val CssOutput = JsRoot.resolve("classes").resolve("css")
}

trait Protocol extends DefaultJsonProtocol {
  implicit val sSFormat: RootJsonFormat[Seq[String]] = seqFormat
}

class Server(implicit fm: FlowMaterializer, system: ActorSystem, executor: ExecutionContextExecutor) extends Directives with Protocol {
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
          path("data" / Segment / Segment / Segment) {(experimentName, date, dataSource) =>
            parameters('paused.as[Option[Boolean]]) { maybePaused =>
              val paused = maybePaused.getOrElse(false)
              val path: Path = ResultsPath.resolve(experimentName).resolve(date).resolve("json").resolve(dataSource)
              if (dataSource == "mutualInformation.json") {
                val actor = system.actorOf(FileStreamActor.props(path, paused, MutualInfos.zero))
                handleWebsocketMessages(Flows.dynamicDataStreamFlow[MutualInfos](actor, bufferSize = 100).via(reportErrorsFlow))
              } else {
                val actor = system.actorOf(FileStreamActor.props(path, paused, 0.0))
                handleWebsocketMessages(Flows.dynamicDataStreamFlow[Double](actor, bufferSize = 100).via(reportErrorsFlow))
              }
            }
          } ~
          path("experiments" / Segment / Segment) { (experimentName, date) =>
            complete {
              // TODO: how do you handle failure?
              Handlers.dirListing(ResultsPath.resolve(experimentName).resolve(date).resolve("json"))
            }
          } ~
          path("experiments" / Segment) { experimentName =>
            complete {
              // TODO: how do you handle failure?
              Handlers.dirListing(ResultsPath.resolve(experimentName))
            }
          } ~
          path("experiments") {
            // TODO: async I/O instead of using a future here?
            complete {
              Handlers.dirListing(ResultsPath)
            }
          }
        } ~
        getFromResourceDirectory("web")
    }
    }

  def reportErrorsFlow[T]: Flow[T, T, Unit] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
      def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

      override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
        println(s"WS stream failed with $cause")
        super.onUpstreamFailure(cause, ctx)
      }
    })

}

