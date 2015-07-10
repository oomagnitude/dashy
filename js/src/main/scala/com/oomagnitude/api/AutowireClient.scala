package com.oomagnitude.api

import org.scalajs.dom
import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import upickle.{default => upickle}

import scala.concurrent.Future

object AutowireClient extends autowire.Client[String, upickle.Reader, upickle.Writer]{
  override def doCall(req: Request): Future[String] = {
    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      data = upickle.write(req.args)
    ).map(_.responseText)
  }

  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)
  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
}