package com.oomagnitude.rx

import rx._

object Buffer {

  /**
   * Create an Rx which buffers the last n values of a given signal
   *
   * @param signal the raw signal to buffer values from
   * @param size the size of the buffer. if none, the buffer will grow unbounded
   * @tparam T the type of item being buffered
   * @return a Rx whose values are the current buffer. Each time the raw signal outputs a new value, it is prepended
   *         to the buffer, and the buffer is truncated to size (if any)
   */
  def create[T](signal: Rx[T], size: Option[Int] = None): Var[List[T]] = {
    val buffer = Var(List.empty[T])

    append(signal, buffer, size)

    buffer
  }

  def append[T](signal: Rx[T], buffer: Var[List[T]], size: Option[Int] = None): Unit = {
    Obs(signal, skipInitial = true) {
      // prepend new value to the buffer
      var items = signal() :: buffer()
      // truncate buffer to max size
      size.foreach(s => items = items.take(s))
      // update the data buffer
      buffer() = items
    }
  }
}

class Buffer[T](signal: Rx[T]) {
  val data = Var(Vector.empty[T])
  var size: Option[Int] = None

  private[this] val obs = Obs(signal, skipInitial = true) {
    // append new value to the buffer
    var items = data() :+ signal()
    // truncate buffer to max size
    size.foreach(s => items = items.drop(items.size - s))
    // update the data buffer
    data() = items
  }
}
