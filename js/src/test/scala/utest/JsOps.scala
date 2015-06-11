package utest

import rx.ops.DomScheduler

import scala.concurrent.{Promise, Future}
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

object JsOps {
  import scala.concurrent.ExecutionContext.Implicits.global
  val scheduler = new DomScheduler

  def eventually(eval: => Try[Boolean])(implicit timeout: FiniteDuration): Future[Boolean] = {
    val p = Promise[Boolean]()
    scheduler.scheduleOnce(timeout) {
      eval match {
        case Success(b) => p.success(b)
        case Failure(t) => p.failure(t)
      }
    }
    p.future
  }
}
