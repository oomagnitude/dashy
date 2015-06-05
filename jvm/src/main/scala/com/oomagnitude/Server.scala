package com.oomagnitude

import java.io.File
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable._
import akka.http.scaladsl.server.Directives
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.stage.{TerminationDirective, SyncDirective, Context, PushStage}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import akka.stream.UniformFanInShape

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
        getFromResource("web/index.html")
      } ~
        // Scala-JS puts them in the root of the resource directory per default,
        // so that's where we pick them up
        pathPrefix("js") {
          path(Segment) { filename =>
            getFromFile(ScalaJSOutput.resolve(filename).toFile)
          }
        } ~
        pathPrefix("css") {
          path(Segment) { filename =>
            getFromFile(CssOutput.resolve(filename).toFile)
          }
        } ~
        pathPrefix("api") {
          path("experiments" / Segment / Segment / Segment) { (experimentName, date, dataSource) =>
            val path = ResultsPath.resolve(experimentName).resolve(date).resolve("json").resolve(dataSource)
            handleWebsocketMessages(Handlers.streamFile(path).via(reportErrorsFlow))
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

