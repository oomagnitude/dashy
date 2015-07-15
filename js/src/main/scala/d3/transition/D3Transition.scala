package d3.transition

import viz.transition._

import scala.scalajs.js

class D3Transition(transition: js.Dynamic) extends Transition {
  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#delay]]
   */
  override def delay(millis: Int): Transition = {
    transition.delay(millis)
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#delay]]
   */
  override def delay: Int = transition.delay().asInstanceOf[Int]


//  override def attrTween[A](attr: Attr, interpolate: Double => A): Transition = {
//    val fn: js.Function1[js.Dynamic, js.Dynamic] = {t: js.Dynamic =>
//      println(s"calling interpolate with $t")
//      println(s"result is: ${interpolate(t.asInstanceOf[Double])}")
//      interpolate(t.asInstanceOf[Double]).asInstanceOf[js.Dynamic]}
//    transition.attrTween(attr.name, {_: js.Dynamic => fn})
//    this
//  }


  override def tween(name: String, interpolate: (Double) => Unit): Transition = {
    val fn: js.Function1[Double, Unit] = interpolate
    transition.tween(name, {(d: js.Dynamic, i: Int) => fn})
    this
  }
//
//  /**
//   * [[https://github.com/mbostock/d3/wiki/Transitions#attr]]
//   */
//  override def attr[A](name: Attr, value: A): Transition = ???

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#d3_ease]]
   */
  override def ease(easeType: String): Transition = ???

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#duration]]
   */
  override def duration: Int = transition.duration.asInstanceOf[Int]

  /**
   * [[https://github.com/mbostock/d3/wiki/Transitions#duration]]
   */
  override def duration(millis: Int): Transition = {
    transition.duration(millis)
    this
  }

}
