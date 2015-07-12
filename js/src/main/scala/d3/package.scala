import d3.layout.D3Layout
import d3.scale.D3Scale

import scala.scalajs.js

package object d3 {
  val d3 = js.Dynamic.global.d3
  
  object all {
    object d3 {
      val layout = D3Layout
      val scale = D3Scale
    }
  }
}
