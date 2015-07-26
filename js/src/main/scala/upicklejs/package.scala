import upickle.{default => upickle}

import scala.scalajs.js
import scala.scalajs.js.JSON

package object upicklejs {
  def read[T: upickle.Reader](d: js.Dynamic): T = upickle.read[T](JSON.stringify(d))

  def write[T: upickle.Writer](item: T): js.Dynamic = JSON.parse(upickle.write(item))
}
