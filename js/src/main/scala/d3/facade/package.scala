package d3

import scalajs.js

package object facade {
  object d3 extends js.GlobalScope {
    val d3: D3Root = js.native
  }
}
