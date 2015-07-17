package com.oomagnitude.view

import com.oomagnitude.css.{Styles => style}
import com.oomagnitude.metrics.model.Metrics._
import com.oomagnitude.model.ChartData
import com.oomagnitude.rx.Rxs._
import d3.all._
import org.scalajs.dom.html.Div
import rx._
import svg.Svg
import viz.scale.LinearScale

import scala.scalajs.js.JSConverters._
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{TypedTag, svgAttrs => sa, svgTags => st}

object Gabor {

  implicit val gaussianOrdering = new Ordering[LocatableGaussian] {
    override def compare(x: LocatableGaussian, y: LocatableGaussian): Int = x.gaussian.mean.compare(y.gaussian.mean)
  }

  def apply(data: ChartData[LabeledGaussians], aspectRatio: Double, params: GaussianParams) = {
    val (width, height) = Svg.dimensions(aspectRatio)
    val svgs = Var(List.empty[TypedTag[Div]])

    // TODO: config for expected range of values. MNIST is reverse (0 means white, 255 means black)
    val colorScale = d3.scale.linear[Double, String]
      .domain(Seq(0.0, 255.0))
      .range(Seq("#ffffff", "#000000"))

    Obs(data.signal) {
      if (data.signal().nonEmpty) {
        val all = data.signal().head._2.value.gaussians
//        val max = all.flatMap(_._2).max
//        val colorScale = d3.scale.linear[Double, String]
//          .domain(Seq(0.0, max.gaussian.mean))
//          .range(Seq("#000000", "#ffffff"))

        svgs() = all.map { case (_, locatables) =>
          val pixels = image(locatables, params, colorScale, height.toInt, width.toInt)
          bs.col3(Svg(aspectRatio)(style.salmonBackground, pixels))
        }
      } else {
        svgs() = List.empty
      }
    }

    val headingText = {
      import rx.ops._
      data.currentTimestep.map(t => s"Timestep $t")
    }
    div(bs.row(bs.col12(h3(headingText.asFrag))), bs.row(svgs.asFrags)).render
  }

  def image(data: List[LocatableGaussian], config: GaussianParams, colorScale: LinearScale[Double, String],
            canvasHeight: Int, canvasWidth: Int) = {

    val heightDomain = (0 to config.geometry.height).toSeq
    val widthDomain = (0 to config.geometry.width).toSeq
    val heightScale = d3.scale.ordinal[Int, Int].domain(heightDomain).rangeRoundBands(0, canvasHeight)
    val widthScale = d3.scale.ordinal[Int, Int].domain(widthDomain.toJSArray).rangeRoundBands(0, canvasWidth)
    val sideLength = math.min(heightScale.rangeBand, widthScale.rangeBand).toInt

    val opacityScale = d3.scale.linear[Double, Double].domain(Seq(0.0, config.maxPrecision)).range(Seq(0.0, 1.0))

    if (data.nonEmpty) {
      data.map { obj =>
        val color = colorScale(obj.gaussian.mean)
        val opacity = opacityScale(obj.gaussian.precision)
        st.rect(sa.x:= sideLength*obj.location.x, sa.y:= sideLength*obj.location.y,
          width:=sideLength, height:=sideLength, sa.opacity:=opacity, sa.fill:=color)
      }
    }
    else List.empty
  }
}
