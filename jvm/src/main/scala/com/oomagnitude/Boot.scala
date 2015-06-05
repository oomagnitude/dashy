package com.oomagnitude

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorFlowMaterializer

import scala.util.{Failure, Success}

object Boot extends App {
  implicit val system = ActorSystem()
  import system.dispatcher
  implicit val materializer = ActorFlowMaterializer()

  val config = system.settings.config
  val interface = config.getString("app.interface")
  val port = config.getInt("app.port")

  val service = new Server

  val binding = Http().bindAndHandle(service.route, interface, port)
  binding.onComplete {
    case Success(binding) ⇒
      val localAddress = binding.localAddress
      println(s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
    case Failure(e) ⇒
      println(s"Binding failed with ${e.getMessage}")
      system.shutdown()
  }
}
