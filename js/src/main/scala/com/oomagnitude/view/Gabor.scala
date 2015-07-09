package com.oomagnitude.view

import com.oomagnitude.collection.CollectionExt._
import com.oomagnitude.css.{Styles => style}
import com.oomagnitude.metrics.model.Metrics.{GaussianParams, LabeledGaussians}
import com.oomagnitude.metrics.model.ext.LocatableGaussian
import com.oomagnitude.model.ChartData
import com.oomagnitude.rx.Rxs._
import d3.{D3Scale, LinearScale}
import org.scalajs.dom.html.Div
import rx._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{TypedTag, svgAttrs => sa, svgTags => st}


object Gabor {
  val d3 = js.Dynamic.global.d3

  implicit val gaussianOrdering = new Ordering[LocatableGaussian] {
    override def compare(x: LocatableGaussian, y: LocatableGaussian): Int = x.gaussian.mean.compare(y.gaussian.mean)
  }

  def apply(data: ChartData[LabeledGaussians], params: GaussianParams) = {
    val dimensions = Dimensions(DefaultHeight, DefaultWidth, Margin(10,10,10,10))
    val svgs = Var(List.empty[TypedTag[Div]])
    Obs(data.signal) {
      if (data.signal().nonEmpty) {
        val all = data.signal().head._2.value.gaussians
        val max = all.flatMap(_._2).max
        val colorScale = D3Scale.linear[Double, String].domain(Seq(0.0, max.gaussian.mean)).range(Seq("#000000", "#ffffff"))

        svgs() = all.map { case (label, locatables) =>
          // TODO: get width and set height accordingly (use aspect ratio)
          val pixels = image(locatables, params, colorScale, dimensions.height, dimensions.width)
          bs.col3(
            div(style.svgContainer,
              st.svg(style.svgContent, style.greyBackground, sa.viewBox:="0 0 500 500",
                sa.preserveAspectRatio:="xMinYMin meet", pixels)))
        }
      } else {
        svgs() = List.empty
      }
    }

    bs.row(svgs.asFrags).render
  }

  def image(data: List[LocatableGaussian], config: GaussianParams, colorScale: LinearScale[Double, String],
            canvasHeight: Int, canvasWidth: Int) = {

    val heightDomain = (0 to config.geometry.height).toSeq
    val widthDomain = (0 to config.geometry.width).toSeq
    val heightScale = D3Scale.ordinal[Int, Int].domain(heightDomain).rangeRoundBands(0, canvasHeight)
    val widthScale = D3Scale.ordinal[Int, Int].domain(widthDomain.toJSArray).rangeRoundBands(0, canvasWidth)
    val sideLength = math.min(heightScale.rangeBand, widthScale.rangeBand).toInt

    val opacityScale = D3Scale.linear[Double, Double].domain(Seq(0.0, config.maxPrecision)).range(Seq(0.0, 1.0))

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
