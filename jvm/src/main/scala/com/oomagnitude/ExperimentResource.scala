package com.oomagnitude

import surf.rest.RESTPath.IntNumber
import surf.{ServiceRefFactory, ServiceRef}
import surf.rest.RESTResource

case class ExperimentResource(service: ServiceRef) extends RESTResource {
  override def name: String = "exp"

  override def handler(implicit factory: ServiceRefFactory): ServiceRef = service

  override def child(path: List[String]): Option[RESTResource] = None
}

case class ExperimentServiceResource(service: ServiceRef) extends RESTResource {
  override def name: String = "experiments"

  override def handler(implicit factory: ServiceRefFactory): ServiceRef = service

  override def child(path: List[String]): Option[RESTResource] = path match {
    case IntNumber(id) => Some(ExperimentResource(service))
    case _ => None
  }
}