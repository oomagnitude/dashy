package d3.shape
import d3._
import viz.layout.{ExpandedNode, TreeLink}
import viz.shape.Diagonal
import scala.scalajs.js


class D3Diagonal extends Diagonal {
  private[this] val shape = d3.svg.diagonal()

  override def apply(datum: TreeLink[ExpandedNode]): String =
    shape(datum.asInstanceOf[js.Any]).asInstanceOf[String]

  override def projection(fn: (Double, Double) => (Double, Double)): this.type = {
    shape.projection({d: js.Dynamic =>
      val (x, y) = fn(d.x.asInstanceOf[Double], d.y.asInstanceOf[Double])
      js.Array(x, y)
    })
    this
  }

  override def apply(datum: TreeLink[ExpandedNode], index: Int): String =
    shape(datum.asInstanceOf[js.Any], index).asInstanceOf[String]
}
