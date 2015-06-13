package com.oomagnitude.rx

import rx._
import rx.ops._

import scala.concurrent.Promise

class CallbackRx[M, T](convert: M => T, initialValue: T) {
  import scala.concurrent.ExecutionContext.Implicits.global
  private val p = Var(Promise[T]())
  
  val callback: M => Unit = { m: M =>
    p().success(convert(m))
    p() = Promise[T]()
  }

  val data: Rx[T] = Rx {p().future}.async(initialValue)

  def close() = data.killAll()
}
