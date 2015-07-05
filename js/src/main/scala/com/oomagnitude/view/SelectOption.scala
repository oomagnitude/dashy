package com.oomagnitude.view

import scala.scalajs.js.Dynamic.{literal => l}

object SelectOption {
  implicit val defaultOrdering = new Ordering[SelectOption] {
    override def compare(x: SelectOption, y: SelectOption): Int = x.name.compare(y.name)
  }
  
  val idOrdering = new Ordering[SelectOption] {
    override def compare(x: SelectOption, y: SelectOption): Int = x.id.compareTo(y.id)
  }
}

case class SelectOption(id: String, name: String) {
  def toJs = l(name = name, value = id)
}
