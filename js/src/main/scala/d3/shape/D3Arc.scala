package d3.shape

import d3._
import viz.shape.{ArcDatum, Arc}
import scala.scalajs.js

class D3Arc extends Arc {

  override def apply[T: ArcDatum](datum: T): String = {
    val s = shape(datum)
    s(js.Dynamic.literal()).asInstanceOf[String]
  }

  override def centroid[T: ArcDatum](datum: T): String = {
    shape(datum).centroid(js.Dynamic.literal()).asInstanceOf[String]
  }

  private def shape[T: ArcDatum](datum: T): js.Dynamic = {
    val ops = implicitly[ArcDatum[T]]
    d3.svg.arc()
      .outerRadius(ops.outerRadius(datum))
      .innerRadius(ops.innerRadius(datum))
      .startAngle(ops.startAngle(datum))
      .endAngle(ops.endAngle(datum))
      .cornerRadius(ops.cornerRadius(datum))
      .padRadius(ops.padRadius(datum))
  }
}
