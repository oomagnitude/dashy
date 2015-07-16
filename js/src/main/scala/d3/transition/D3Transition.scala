package d3.transition

import d3._
import viz.selection.Selection
import viz.transition.Transition

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.JSConverters._

class D3Transition[T](selection: Selection[T, _], name: String = "") extends Transition[T] {
  private[this] val transition = {
    val data = selection.data.indices.map(i => literal(index = i))
    d3.selectAll(selection.elements.toJSArray).data(data.toJSArray).transition(name)
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#delay]]
   */
  override def delay(millis: Int): Transition[T] = {
    transition.delay(millis)
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#delay]]
   */
  override def delay(fn: (T, Int) => Int): Transition[T] = {
    transition.delay({(d: js.Dynamic, index: Int) => fn(selection.data(index), index)})
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#delay]]
   */
  override def delay: Int = transition.delay().asInstanceOf[Int]

  override def tween(name: String, factory: (T, Int) => Double => Unit): Transition[T] = {
    transition.tween(name, {(d: js.Dynamic, i: Int) =>
      val fn: js.Function1[Double, Unit] = factory(selection.data(i), i)
      fn
    })
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_ease]]
   */
  override def ease(easeType: String): Transition[T] = ???

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#duration]]
   */
  override def duration: Int = transition.duration.asInstanceOf[Int]

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#duration]]
   */
  override def duration(millis: Int): Transition[T] = {
    transition.duration(millis)
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#duration]]
   */
  override def duration(fn: (T, Int) => Int): Transition[T] = {
    transition.duration({(d: js.Dynamic, index: Int) => fn(selection.data(index), index)})
    this
  }
}
