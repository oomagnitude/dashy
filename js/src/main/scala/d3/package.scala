import d3.layout.D3Layout

import scala.scalajs.js

package object d3 {
  val d3 = js.Dynamic.global.d3
  
  object all {
    val layout = D3Layout
  }
}
