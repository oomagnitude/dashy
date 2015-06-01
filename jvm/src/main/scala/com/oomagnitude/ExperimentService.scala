package com.oomagnitude

import surf.rest.RESTResponse.{NotFound, OK}
import surf.rest.{RESTResponse, RESTResource, RESTService}
import upickle._

class ExperimentService extends RESTService {
  private var experiments = List("alice", "block-eater", "thermostat")
  private var nextId = 1

  override def handleGET(resource: RESTResource, params: Map[String, Any]): Unit = resource match {
    case _ => request ! OK(write(experiments))
  }
}


