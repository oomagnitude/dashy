package d3

// TODO: put this stuff into a separate library

trait Invertible[D, R] {
  def invert(y: R): D
}

trait Clampable[S] {
  def clamp(c: Boolean): S
  def clamp: Boolean
}

trait Roundable[R, S] {
  def rangeRound(values: Seq[R]): S
}

trait Ticks[D] {
  def ticks: Seq[D]
  def tickFormat(count: Int): Double => String
  def tickFormat(count: Int, format: String): Double => String
}

trait ConfigurableTicks[D] {
  def ticks(count: Int): Seq[D]
}

trait InvertibleExtent[D, R] {
  def invertExtent(y: R): (D, D)
}

trait Interpolatable[D, R, S] {
  def interpolate: (D, R) => (Double => R)
  def interpolate(factory: (D, R) => (Double => R)): S
}


trait LinearScale[D, R] extends Scale[D, R, LinearScale[D, R]]
    with Invertible[D, R] with Clampable[LinearScale[D, R]]
    with Roundable[R, LinearScale[D, R]] with Ticks[D]
    with ConfigurableTicks[D] with Interpolatable[D, R, LinearScale[D, R]] {
  def nice(count: Int): LinearScale[D, R]
}

trait IdentityScale[D] extends Scale[D, D, IdentityScale[D]]
    with Invertible[D, D] with Ticks[D] with ConfigurableTicks[D]

trait LogScale[D, R] extends Scale[D, R, LogScale[D, R]] with Invertible[D, R]
    with Clampable[LogScale[D, R]] with Roundable[R, LogScale[D, R]]
    with Ticks[D] with Interpolatable[D, R, LogScale[D, R]] {
  def nice(): LogScale[D, R]

  def base(b: Double): LogScale[D, R]
  def base: Double
}

trait PowerScale[D, R] extends Scale[D, R, PowerScale[D, R]] with Invertible[D, R]
    with Clampable[PowerScale[D, R]] with Roundable[R, PowerScale[D, R]]
    with Ticks[D] with ConfigurableTicks[D] with Interpolatable[D, R, PowerScale[D, R]] {
  def nice(tickCount: Int): PowerScale[D, R]
  def nice(): PowerScale[D, R]
  def exponent(k: Double): PowerScale[D, R]
  def exponent: Double
}

// D type must be coercible to a js.Date
trait TimeScale[D, R] extends Scale[D, R, TimeScale[D, R]] with Invertible[D, R]
    with Roundable[R, TimeScale[D, R]] with Interpolatable[D, R, TimeScale[D, R]]
    with Clampable[TimeScale[D, R]] with ConfigurableTicks[D] {
  // TODO:
  // nice([interval[, step]])
  // nice([count])
  // ticks([interval[, step]])
  def tickFormat(count: Int): Double => String
}

trait ThresholdScale[D, R] extends Scale[D, R, ThresholdScale[D, R]] with InvertibleExtent[D, R]

trait OrdinalScale[D, R] extends Scale[D, R, OrdinalScale[D, R]] {
  def rangePoints(min: R, max: R, padding: Int = 0): OrdinalScale[D, R]
  def rangeRoundPoints(min: R, max: R, padding: Int = 0): OrdinalScale[D, R]
  def rangeBands(min: R, max: R): OrdinalScale[D, R]
  def rangeBands(min: R, max: R, padding: Int, outerPadding: Int = 0): OrdinalScale[D, R]
  def rangeRoundBands(min: R, max: R): OrdinalScale[D, R]
  def rangeRoundBands(min: R, max: R, padding: Int, outerPadding: Int = 0): OrdinalScale[D, R]
  def rangeBand: Double
  def rangeExtent: Extent[R]
}

trait QuantizeScale[D, R] extends Scale[D, R, QuantizeScale[D, R]] with InvertibleExtent[D, R]

trait QuantileScale[D, R] extends Scale[D, R, QuantileScale[D, R]] with InvertibleExtent[D, R] {
  def quantiles(): Seq[R]
}

/**
 * See https://github.com/mbostock/d3/wiki/Scales
 *
 * @tparam D the type of value in the domain of the scale
 * @tparam R the type of value in the range of the scale
 * @tparam S the type of scale object (used to configure the scale bit-by-bit)
 */
trait Scale[D, R, S <: Scale[D, R, S]] {
  def apply(x: D): R
  def domain: Seq[D]
  def domain(points: Seq[D]): S
  def range: Seq[R]
  def range(points: Seq[R]): S
}

trait Scales {
  def linear[D, R]: LinearScale[D, R]
  def ordinal[D, R]: OrdinalScale[D, R]
  def identity[D]: IdentityScale[D]
  def log[D, R]: LogScale[D, R]
  def time[D, R]: TimeScale[D, R]
  def timeUtc[D, R]: TimeScale[D, R]
  def threshold[D, R]: ThresholdScale[D, R]
  def quantize[D, R]: QuantizeScale[D, R]
  def quantile[D, R]: QuantileScale[D, R]
  def colorScale(data: Iterable[Double], colors: Seq[String]): LinearScale[Double, String]
}
