package com.oomagnitude

import akka.actor.ActorSystem
import surf.akka.ServiceActorRefFactory
import surf.akka.rest.{RESTRouter, SimpleRESTServer}
import surf.rest.StaticRESTResource
import surf.{ServiceProps, ServiceRefRegistry}

object Server extends App {

  val actorSystem = ActorSystem("dashboard")


  val serviceFactory = ServiceActorRefFactory(actorSystem)
  val serviceRegistry = ServiceRefRegistry(serviceFactory, "ExperimentService" -> ServiceProps(new ExperimentService))

  val restRoot = StaticRESTResource("api",
    new ExperimentServiceResource(serviceRegistry.serviceAt("ExperimentService"))
  )


  val httpServer = SimpleRESTServer.fromRoute(actorSystem = actorSystem){ (ec, cf, fm) =>
    import akka.http.server.Directives._
    implicit val executionContext = ec

    /* START PAGE */
    path("") {
      getFromFile("jvm/src/main/resources/web/index.html")
    } ~
      /* RESOURCES */
      path("web" / Rest ) { path =>
        getFromFile("jvm/src/main/resources/web/"+path) ~
          getFromFile("js/target/scala-2.11/"+path)
      } ~
      /* REST */
      RESTRouter("api", restRoot)(cf,serviceFactory,ec,fm)

  }

  println("press ENTER to stop the server")
  Console.in.readLine()
  httpServer.stop()

}
