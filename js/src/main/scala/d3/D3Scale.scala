package d3

import scala.scalajs.js
import js.JSConverters._

object D3Scale extends Scales {
  override def linear[D, R]: LinearScale[D, R] = new D3LinearScale[D, R]()
  override def ordinal[D, R]: OrdinalScale[D, R] = new D3OrdinalScale[D, R]()
  override def identity[D]: IdentityScale[D] = new D3IdentityScale[D]()
  override def log[D, R]: LogScale[D, R] = new D3LogScale[D, R]()
  override def time[D, R]: TimeScale[D, R] = new D3TimeScale[D, R]()
  override def timeUtc[D, R]: TimeScale[D, R] = new D3TimeScale[D, R](d3.time.scale.utc())
  override def threshold[D, R]: ThresholdScale[D, R] = new D3ThresholdScale[D, R]()
  override def quantize[D, R]: QuantizeScale[D, R] = new D3QuantizeScale[D, R]()
  override def quantile[D, R]: QuantileScale[D, R] = new D3QuantileScale[D, R]()
}

trait D3ScaleBuilder[S] {
  def build(scale: js.Dynamic): S
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

trait D3Clampable[S] extends Clampable[S] {
  self: HasD3Scale with D3ScaleBuilder[S] =>
  
  override def clamp: Boolean = scale.clamp().asInstanceOf[Boolean]

  override def clamp(c: Boolean): S = build(scale.copy().clamp(c))
}

trait D3Roundable[R, S] extends Roundable[R, S] {
  self: HasD3Scale with D3ScaleBuilder[S] =>

  override def rangeRound(values: Seq[R]): S = build(scale.copy().rangeRound(values.map(_.asInstanceOf[js.Any]).toJSArray))
}

trait D3Interpolatable[D, R, S] extends Interpolatable[D, R, S] {
  self: HasD3Scale with D3ScaleBuilder[S] =>
  override def interpolate(factory: (D, R) => (Double) => R): S = ???

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

abstract class D3Scale[D, R, S <: Scale[D, R, S]](override val scale: js.Dynamic, factory: js.Dynamic => S)
    extends Scale[D, R, S] with HasD3Scale with D3ScaleBuilder[S] {

  override def apply(x: D): R = scale(x.asInstanceOf[js.Any]).asInstanceOf[R]

  override def range(points: Seq[R]): S = build(scale.copy().range(points.toJSArray))

  override def range: Seq[R] = scale.range().asInstanceOf[js.Array[js.Dynamic]].map(_.asInstanceOf[R])

  override def domain(points: Seq[D]): S = build(scale.copy().domain(points.toJSArray))

  override def domain: Seq[D] = scale.domain().asInstanceOf[js.Array[js.Dynamic]].map(_.asInstanceOf[D])

  override def build(scale: js.Dynamic): S = factory(scale)
}

class D3LinearScale[D, R](scale: js.Dynamic = d3.scale.linear())
    extends D3Scale[D, R, LinearScale[D, R]](scale, {s => new D3LinearScale[D, R](s)})
    with LinearScale[D, R] with D3Invertible[D, R] with D3Clampable[LinearScale[D, R]]
    with D3Roundable[R, LinearScale[D, R]] with D3Interpolatable[D, R, LinearScale[D, R]]
    with D3Ticks[D] with D3ConfigurableTicks[D] {
  override def nice(count: Int): LinearScale[D, R] = new D3LinearScale[D, R](scale.copy().nice(count))
}

class D3IdentityScale[D](scale: js.Dynamic = d3.scale.identity())
  extends D3Scale[D, D, IdentityScale[D]](scale, {s => new D3IdentityScale[D](s)})
  with IdentityScale[D] with D3Invertible[D, D] with D3Ticks[D] with D3ConfigurableTicks[D]

class D3LogScale[D, R](scale: js.Dynamic = d3.scale.log())
    extends D3Scale[D, R, LogScale[D, R]](scale, {s => new D3LogScale[D, R](s)})
    with LogScale[D, R] with D3Invertible[D, R]
    with D3Clampable[LogScale[D, R]] with D3Roundable[R, LogScale[D, R]]
    with D3Ticks[D] with D3Interpolatable[D, R, LogScale[D, R]] {

  override def nice(): LogScale[D, R] = new D3LogScale[D, R](scale.copy().nice())

  override def base: Double = scale.base().asInstanceOf[Double]

  override def base(b: Double): LogScale[D, R] = new D3LogScale[D, R](scale.copy().base(b))
}

class D3PowerScale[D, R](scale: js.Dynamic = d3.scale.pow())
    extends D3Scale[D, R, PowerScale[D, R]](scale, {s => new D3PowerScale[D, R](s)})
    with PowerScale[D, R] with D3Invertible[D, R]
    with D3Clampable[PowerScale[D, R]] with D3Roundable[R, PowerScale[D, R]]
    with D3Ticks[D] with D3ConfigurableTicks[D] with D3Interpolatable[D, R, PowerScale[D, R]] {
  override def nice(tickCount: Int): PowerScale[D, R] = new D3PowerScale[D, R](scale.copy().nice(tickCount))

  override def nice(): PowerScale[D, R] = new D3PowerScale[D, R](scale.copy().nice())

  override def exponent(k: Double): PowerScale[D, R] = new D3PowerScale[D, R](scale.copy().exponent(k))

  override def exponent: Double = scale.exponent().asInstanceOf[Double]
}

class D3TimeScale[D, R](scale: js.Dynamic = d3.time.scale())
  extends D3Scale[D, R, TimeScale[D, R]](scale, {s => new D3TimeScale[D, R](s)})
  with TimeScale[D, R] with D3Invertible[D, R]
  with D3Roundable[R, TimeScale[D, R]] with D3Interpolatable[D, R, TimeScale[D, R]]
  with D3Clampable[TimeScale[D, R]] with D3ConfigurableTicks[D] {
  override def tickFormat(count: Int): (Double) => String = scale.tickFormat(count).asInstanceOf[(Double) => String]
}

class D3OrdinalScale[D, R](scale: js.Dynamic = d3.scale.ordinal())
    extends D3Scale[D, R, OrdinalScale[D, R]](scale, {s => new D3OrdinalScale[D, R](s)})
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
    extends D3Scale[D, R, ThresholdScale[D, R]](scale, {s => new D3ThresholdScale[D, R](s)})
    with ThresholdScale[D, R] with D3InvertibleExtent[D, R]

class D3QuantizeScale[D, R](scale: js.Dynamic = d3.scale.quantize())
    extends D3Scale[D, R, QuantizeScale[D, R]](scale, {s => new D3QuantizeScale[D, R](s)})
    with QuantizeScale[D, R] with D3InvertibleExtent[D, R]

class D3QuantileScale[D, R](scale: js.Dynamic = d3.scale.quantile())
  extends D3Scale[D, R, QuantileScale[D, R]](scale, {s => new D3QuantileScale[D, R](s)})
  with QuantileScale[D, R] with D3InvertibleExtent[D, R] {
  override def quantiles(): Seq[R] = scale.quantiles().asInstanceOf[js.Array[js.Any]].map(_.asInstanceOf[R])
}