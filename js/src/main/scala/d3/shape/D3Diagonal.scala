package d3.shape
import d3._
import viz.layout.{Coordinate, TreeLink}
import viz.shape.Diagonal

import scala.scalajs.js


class D3Diagonal(shape: js.Dynamic = d3.svg.diagonal()) extends Diagonal {

  override def apply(datum: TreeLink[Coordinate]): String =
    shape(upicklejs.write(datum)).asInstanceOf[String]

  override def projection(fn: (Double, Double) => (Double, Double)): this.type = {
    shape.projection({d: js.Dynamic =>
      val (x, y) = fn(d.x.asInstanceOf[Double], d.y.asInstanceOf[Double])
      js.Array(x, y)
    })
    this
  }

  override def apply(datum: TreeLink[Coordinate], index: Int): String =
    shape(datum.asInstanceOf[js.Any], index).asInstanceOf[String]
}
