package nvd3

import upickle.Js
import scala.scalajs.js
import scala.scalajs.js.JSON

object Margin {
  def create(top: Int, right: Int, bottom: Int, left: Int): Margin = {
    Margin(Some(top), Some(right), Some(bottom), Some(left))
  }

  implicit val marginWriter = upickle.Writer[Margin]{
    case m =>
      Js.Obj(fields(m).map(kv => (kv._1, Js.Num(kv._2))): _*).asInstanceOf[Js.Value]
  }

  implicit val marginReader = upickle.Reader[Margin]{
    case obj: Js.Obj =>
      val map = obj.value.toMap
      Margin(map.get("top").map(_.value.asInstanceOf[Int]),
        map.get("right").map(_.value.asInstanceOf[Int]),
        map.get("bottom").map(_.value.asInstanceOf[Int]),
        map.get("left").map(_.value.asInstanceOf[Int]))
  }

  private def fields(m: Margin) = Seq(m.top.map(("top", _)), m.right.map(("right", _)), m.bottom.map(("bottom", _)),
    m.left.map(("left", _))).flatten
}

case class Margin(top: Option[Int], right: Option[Int], bottom: Option[Int], left: Option[Int]) {
  def asJs: js.Object = JSON.parse(upickle.write(this)).asInstanceOf[js.Object]
}