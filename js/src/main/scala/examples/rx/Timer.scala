package examples.rx

import com.oomagnitude.api.Uris
import com.oomagnitude.rx.CallbackRx
import com.oomagnitude.rx.Rxs._
import org.scalajs.dom.html
import org.scalajs.dom.raw.{MessageEvent, WebSocket}

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object Timer {
  @JSExport
  def main(container: html.Div): Unit = {
    val callbackRx = new CallbackRx({e: MessageEvent => e.data.toString}, "")
    val webSocket = new WebSocket(Uris.webSocketAddress + "/ws/time")
    webSocket.onmessage = callbackRx.callback

    container.appendChild(h1(callbackRx.data.asFrag).render)
  }
}
