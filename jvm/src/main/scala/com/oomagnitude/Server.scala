package com.oomagnitude

import java.io.File
import java.nio.file.{Path, Paths}

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.stage.{Context, PushStage, SyncDirective, TerminationDirective}
import com.oomagnitude.pages.Page
import com.oomagnitude.streams.{Flows, StreamDispatch}
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
        path("angular") {
          getFromResource("web/index.html")
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
            val path: Path = ResultsPath.resolve(experimentName).resolve(date).resolve("json").resolve(dataSource)
            val dispatch = system.actorOf(Props(classOf[StreamDispatch], path, fm))
            handleWebsocketMessages(Flows.dynamicDataStreamFlow(dispatch, bufferSize = 100).via(reportErrorsFlow))
          } ~
//          pathPrefix("experimental") {
//            path("data" / Segment / Segment / Segment) {(experimentName, date, dataSource) =>
//              val path: Path = ResultsPath.resolve(experimentName).resolve(date).resolve("json").resolve(dataSource)
//              val dispatch = system.actorOf(Props(classOf[StreamDispatch], path, fm))
//              handleWebsocketMessages(Flows.dynamicDataStreamFlow(dispatch, bufferSize = 100).via(reportErrorsFlow))
//            }
//          } ~
//          path("data" / Segment / Segment / Segment) { (experimentName, date, dataSource) =>
//            parameters('initialBatchSize.as[Int], 'timestepResolution.as[Option[Int]],
//              'dataPointFrequencySeconds.as[Option[Int]]).as(DataSourceFetchParams) { params =>
//              val path = ResultsPath.resolve(experimentName).resolve(date).resolve("json").resolve(dataSource)
//              handleWebsocketMessages(Handlers.streamFile(path, params).via(reportErrorsFlow))
//            }
//          } ~
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

