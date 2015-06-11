package com.oomagnitude.angular

import scala.scalajs.js

object JsImplicits {
  implicit class DynamicOps(val self: js.Dynamic) extends AnyVal {
    def asArray: js.Array[js.Dynamic] = self.asInstanceOf[js.Array[js.Dynamic]]
  }
}
