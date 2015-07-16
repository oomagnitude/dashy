package viz.transition

trait Transition[T] {

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#delay]]
   */
  def delay(millis: Int): Transition[T]
  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#delay]]
   */
  def delay(fn: (T, Int) => Int): Transition[T]
  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#delay]]
   */
  def delay: Int

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#duration]]
   */
  def duration(millis: Int): Transition[T]
  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#duration]]
   */
  def duration(fn: (T, Int) => Int): Transition[T]

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#duration]]
   */
  def duration: Int

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_ease]]
   */
  // TODO: optional arguments
  // TODO: custom easing function
  def ease(easeType: String): Transition[T]

  //  /**
  //   * [[https://github.com/mbostock/d3/wiki/Transitions#attr]]
  //   */
  //  def attr[A](name: Attr, value: A): Transition[T]
  //  /**
  //   * [[https://github.com/mbostock/d3/wiki/Transitions#attr]]
  //   */
  //  def attr[A](name: Attr, value: T => A): Transition[T]
  //  /**
  //   * [[https://github.com/mbostock/d3/wiki/Transitions#attr]]
  //   */
  //  def attr[A](name: Attr, value: (T, Int) => A): Transition[T]

  //  /**
  //   * [[https://github.com/mbostock/d3/wiki/Transitions#attrTween]]
  //   */
  //  def attrTween[A](name: Attr, tween: (T, Int) => Double => A): Transition[T]
  //  /**
  //   * [[https://github.com/mbostock/d3/wiki/Transitions#attrTween]]
  //   */
  //  def attrTween[A](name: Attr, tween: T => Double => A): Transition[T]

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#tween]]
   */
  def tween(name: String, factory: (T, Int) => Double => Unit): Transition[T]
}
