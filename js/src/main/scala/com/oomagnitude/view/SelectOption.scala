package com.oomagnitude.view

import scala.scalajs.js.Dynamic.{literal => l}

case class SelectOption(name: String, value: String) {
  def toJs = l(name = name, value = value)
}
