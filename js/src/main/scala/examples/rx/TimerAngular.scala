package examples.rx

import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.core.Scope
import com.oomagnitude.api.Uris
import org.scalajs.dom.html
import org.scalajs.dom.raw.{MessageEvent, WebSocket}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._
import com.greencatsoft.angularjs.AbstractController

@JSExport
object TimerAngular extends js.JSApp {
  val ngApp = "ng-app".attr
  val ngController = "ng-controller".attr

  @JSExport
  override def main(): Unit = {
    val module = Angular.module("timer", Seq())
    module.controller[TimerController]
  }
}

trait TimerScope extends Scope {
  var time: String = js.native
}

@JSExport
@injectable("timerController")
class TimerController(scope: TimerScope) extends AbstractController[TimerScope](scope) {
  scope.time = ""

  val timerWs = new WebSocket(Uris.webSocketAddress + "/ws/time")
  timerWs.onmessage = {e: MessageEvent =>
    scope.$apply(scope.time = e.data.toString)
  }
}
