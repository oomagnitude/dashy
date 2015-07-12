package d3.scale

import com.oomagnitude.collection.CollectionExt._
import d3._
import viz.scale._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object D3Scale extends Scales {
  val GreenGradient = IndexedSeq("#f7fcfd", "#e5f5f9","#ccece6", "#99d8c9", "#66c2a4", "#41ae76", "#238b45", "#006d2c",
    "#00441b")
  val RedGradient = IndexedSeq("#f7fcfd","#e5f5f9","#ccece6", "#99d8c9", "#66c2a4", "#41ae76", "#238b45", "#006d2c",
    "#00441b")

  override def linear[D, R]: LinearScale[D, R] = new D3LinearScale[D, R]()
  override def ordinal[D, R]: OrdinalScale[D, R] = new D3OrdinalScale[D, R]()
  override def identity[D]: IdentityScale[D] = new D3IdentityScale[D]()
  override def log[D, R]: LogScale[D, R] = new D3LogScale[D, R]()
  override def time[D, R]: TimeScale[D, R] = new D3TimeScale[D, R]()
  override def timeUtc[D, R]: TimeScale[D, R] = new D3TimeScale[D, R](d3.time.scale.utc())
  override def threshold[D, R]: ThresholdScale[D, R] = new D3ThresholdScale[D, R]()
  override def quantize[D, R]: QuantizeScale[D, R] = new D3QuantizeScale[D, R]()
  override def quantile[D, R]: QuantileScale[D, R] = new D3QuantileScale[D, R]()

  override def colorScale(data: Iterable[Double], colors: Seq[String]): LinearScale[Double, String] = {
    require(colors.size > 1, s"color scale must provide at least 2 colors for $colors")

    val (min, max) = data.minAndMax
    val extent = max - min
    val increment = extent / (colors.size - 1)
    val domain = colors.indices.map(min + increment * _)

    linear[Double, String].domain(domain).range(colors)
  }
}

trait HasD3Scale {
  protected def scale: js.Dynamic
}

trait D3Invertible[D, R] extends Invertible[D, R] {
  self: HasD3Scale =>
  
  override def invert(y: R): D = scale.invert(y.asInstanceOf[js.Any]).asInstanceOf[D]
}

trait D3InvertibleExtent[D, R] extends InvertibleExtent[D, R] {
  self: HasD3Scale =>
  override def invertExtent(y: R): (D, D) = {
    val seq = scale.invertExtent(y.asInstanceOf[js.Any]).asInstanceOf[js.Array[js.Any]]
    (seq(0).asInstanceOf[D], seq(1).asInstanceOf[D])
  }
}

trait D3Clampable extends Clampable {
  self: HasD3Scale =>
  
  override def clamp: Boolean = scale.clamp().asInstanceOf[Boolean]

  override def clamp(c: Boolean): this.type = {
    scale.clamp(c)
    this
  }
}

trait D3Roundable[R] extends Roundable[R] {
  self: HasD3Scale =>

  override def rangeRound(values: Seq[R]): this.type = {
    scale.rangeRound(values.map(_.asInstanceOf[js.Any]).toJSArray)
    this
  }
}

trait D3Interpolatable[D, R] extends Interpolatable[D, R] {
  self: HasD3Scale =>
  override def interpolate(factory: (D, R) => (Double) => R): this.type = ???

  override def interpolate: (D, R) => (Double) => R = ???
}

trait D3Ticks[D] extends Ticks[D] {
  self: HasD3Scale =>
  override def tickFormat(count: Int, format: String): (Double) => String =
    scale.tickFormat(count, format).asInstanceOf[(Double) => String]

  override def tickFormat(count: Int): (Double) => String =
    scale.tickFormat(count).asInstanceOf[(Double) => String]

  override def ticks: Seq[D] = scale.ticks().asInstanceOf[js.Array[js.Any]].map(_.asInstanceOf[D])
}

trait D3ConfigurableTicks[D] extends ConfigurableTicks[D] {
  self: HasD3Scale =>
  override def ticks(count: Int): Seq[D] = scale.ticks(count).asInstanceOf[js.Array[js.Any]].map(_.asInstanceOf[D])
}

abstract class D3Scale[D, R](override val scale: js.Dynamic)
    extends Scale[D, R] with HasD3Scale {

  override def apply(x: D): R = scale(x.asInstanceOf[js.Any]).asInstanceOf[R]

  override def range(points: Seq[R]): this.type = {
    scale.range(points.toJSArray)
    this
  }

  override def range: Seq[R] = scale.range().asInstanceOf[js.Array[js.Dynamic]].map(_.asInstanceOf[R])

  override def domain(points: Seq[D]): this.type = {
    scale.domain(points.toJSArray)
    this
  }

  override def domain: Seq[D] = scale.domain().asInstanceOf[js.Array[js.Dynamic]].map(_.asInstanceOf[D])
}

class D3LinearScale[D, R](scale: js.Dynamic = d3.scale.linear())
    extends D3Scale[D, R](scale)
    with LinearScale[D, R] with D3Invertible[D, R] with D3Clampable
    with D3Roundable[R] with D3Interpolatable[D, R]
    with D3Ticks[D] with D3ConfigurableTicks[D] {
  override def nice(count: Int): LinearScale[D, R] = new D3LinearScale[D, R](scale.copy().nice(count))
}

class D3IdentityScale[D](scale: js.Dynamic = d3.scale.identity())
  extends D3Scale[D, D](scale)
  with IdentityScale[D] with D3Invertible[D, D] with D3Ticks[D] with D3ConfigurableTicks[D]

class D3LogScale[D, R](scale: js.Dynamic = d3.scale.log())
    extends D3Scale[D, R](scale)
    with LogScale[D, R] with D3Invertible[D, R]
    with D3Clampable with D3Roundable[R]
    with D3Ticks[D] with D3Interpolatable[D, R] {

  override def nice(): LogScale[D, R] = new D3LogScale[D, R](scale.copy().nice())

  override def base: Double = scale.base().asInstanceOf[Double]

  override def base(b: Double): LogScale[D, R] = new D3LogScale[D, R](scale.copy().base(b))
}

class D3PowerScale[D, R](scale: js.Dynamic = d3.scale.pow())
    extends D3Scale[D, R](scale)
    with PowerScale[D, R] with D3Invertible[D, R]
    with D3Clampable with D3Roundable[R]
    with D3Ticks[D] with D3ConfigurableTicks[D] with D3Interpolatable[D, R] {
  override def nice(tickCount: Int): PowerScale[D, R] = new D3PowerScale[D, R](scale.copy().nice(tickCount))

  override def nice(): PowerScale[D, R] = new D3PowerScale[D, R](scale.copy().nice())

  override def exponent(k: Double): PowerScale[D, R] = new D3PowerScale[D, R](scale.copy().exponent(k))

  override def exponent: Double = scale.exponent().asInstanceOf[Double]
}

class D3TimeScale[D, R](scale: js.Dynamic = d3.time.scale())
  extends D3Scale[D, R](scale)
  with TimeScale[D, R] with D3Invertible[D, R]
  with D3Roundable[R] with D3Interpolatable[D, R]
  with D3Clampable with D3ConfigurableTicks[D] {
  override def tickFormat(count: Int): (Double) => String = scale.tickFormat(count).asInstanceOf[(Double) => String]
}

class D3OrdinalScale[D, R](scale: js.Dynamic = d3.scale.ordinal())
    extends D3Scale[D, R](scale)
    with OrdinalScale[D, R] {
  override def rangePoints(min: R, max: R, padding: Int): OrdinalScale[D, R] =
    new D3OrdinalScale[D, R](scale.copy().rangePoints(js.Array(min, max), padding))

  override def rangeRoundBands(min: R, max: R): OrdinalScale[D, R] =
    new D3OrdinalScale[D, R](scale.copy().rangeRoundBands(js.Array(min, max)))

  override def rangeRoundBands(min: R, max: R, padding: Int, outerPadding: Int): OrdinalScale[D, R] =
    new D3OrdinalScale[D, R](scale.copy().rangeRoundBands(js.Array(min, max), padding, outerPadding))

  override def rangeBand: Double = scale.rangeBand().asInstanceOf[Double]

  override def rangeExtent: Extent[R] = {
    val ext = scale.rangeExtent().asInstanceOf[js.Array[js.Dynamic]]
    Extent(ext(0).asInstanceOf[R], ext(1).asInstanceOf[R])
  }

  override def rangeRoundPoints(min: R, max: R, padding: Int): OrdinalScale[D, R] =
    new D3OrdinalScale[D, R](scale.copy().rangeRoundPoints(js.Array(min, max), padding))

  override def rangeBands(min: R, max: R): OrdinalScale[D, R] =
    new D3OrdinalScale[D, R](scale.copy().rangeBands(js.Array(min, max)))

  override def rangeBands(min: R, max: R, padding: Int, outerPadding: Int): OrdinalScale[D, R] =
    new D3OrdinalScale[D, R](scale.copy().rangeBands(js.Array(min, max), padding, outerPadding))
}

class D3ThresholdScale[D, R](scale: js.Dynamic = d3.scale.threshold())
    extends D3Scale[D, R](scale)
    with ThresholdScale[D, R] with D3InvertibleExtent[D, R]

class D3QuantizeScale[D, R](scale: js.Dynamic = d3.scale.quantize())
    extends D3Scale[D, R](scale)
    with QuantizeScale[D, R] with D3InvertibleExtent[D, R]

class D3QuantileScale[D, R](scale: js.Dynamic = d3.scale.quantile())
  extends D3Scale[D, R](scale)
  with QuantileScale[D, R] with D3InvertibleExtent[D, R] {
  override def quantiles(): Seq[R] = scale.quantiles().asInstanceOf[js.Array[js.Any]].map(_.asInstanceOf[R])
}