package d3.facade

import scale.Scales
import layout.Layouts

import scala.scalajs.js

trait D3Root extends Selectors {
  var scale: Scales = js.native
  var layout: Layouts = js.native
}
