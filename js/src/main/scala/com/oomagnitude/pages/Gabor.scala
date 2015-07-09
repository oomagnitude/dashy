package com.oomagnitude.pages

import com.oomagnitude.css.{Styles => style}
import com.oomagnitude.metrics.model.Metrics.{GaussianParams, LabeledGaussians}
import com.oomagnitude.metrics.model.ext.{Gaussian, LocatableGaussian}
import com.oomagnitude.metrics.model.geometry.{Coordinate2D, Geometry2D}
import com.oomagnitude.rx.Rxs._
import com.oomagnitude.svg.{Transform => t}
import com.oomagnitude.view._
import d3.D3OrdinalScale
import org.scalajs.dom.html
import rx._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport
import scala.util.Random
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}

@JSExport
object Gabor {
  val d3 = js.Dynamic.global.d3

  val maxPrecision = 100.0
  val maxValue = 100.0
  val geometry = Geometry2D(60,25)
  val objects = Var(LabeledGaussians(List(("name", {for {
    x <- 0 to geometry.width
    y <- 0 to geometry.height
    coordinate = Coordinate2D(x,y)
    gaussian = Gaussian(mean = Random.nextDouble() * maxValue, precision = Random.nextDouble() * maxPrecision)
  } yield LocatableGaussian(coordinate, gaussian)}.toList))))

  @JSExport
  def main(container: html.Div): Unit = {
    val dimensions = Dimensions(DefaultHeight, DefaultWidth, Margin(10,10,10,10))
    val objectElements = squares(objects, GaussianParams(geometry, maxPrecision), dimensions.innerHeight, dimensions.innerWidth)

    val inner = st.g(sa.transform:=t.translate(dimensions.margin.left,dimensions.margin.top), objectElements.asFrags)
    val svgElement = st.svg(style.greyBackground,
      height := dimensions.height,
      width := dimensions.width, inner)
    container.appendChild(svgElement.render)
  }

  private def squares(data: Rx[LabeledGaussians], config: GaussianParams, canvasHeight: Int, canvasWidth: Int) = {
    val heightDomain = (0 to config.geometry.height).toSeq
    val widthDomain = (0 to config.geometry.width).toSeq
    val heightScale = new D3OrdinalScale[Int, Int]().domain(heightDomain).rangeRoundBands(0, canvasHeight)
    val widthScale = new D3OrdinalScale[Int, Int]().domain(widthDomain.toJSArray).rangeRoundBands(0, canvasWidth)

    val opacityScale = d3.scale.linear().domain(js.Array(0, maxPrecision)).range(js.Array(0, 1))
    val colorScale = d3.scale.linear().domain(js.Array(0, 100)).range(js.Array("#000000", "#ffffff"))

    val sideLength = math.min(heightScale.rangeBand, widthScale.rangeBand).toInt

    Rx {
      data().gaussians.head._2.map { obj =>
        val color = colorScale(obj.gaussian.mean).asInstanceOf[String]
        val opacity = opacityScale(obj.gaussian.precision).asInstanceOf[Double]
        st.rect(sa.x:= sideLength*obj.location.x, sa.y:= sideLength*obj.location.y,
          width:=sideLength, height:=sideLength, sa.opacity:=opacity, sa.fill:=color)
      }
    }
  }
}
