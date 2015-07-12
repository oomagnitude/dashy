package viz.layout

import rx._

trait Point {
  def x: Rx[Option[Double]]
  def y: Rx[Option[Double]]
}

class VarPoint extends Point {
  private[this] val _x: Var[Option[Double]] = Var(None)
  private[this] val _y: Var[Option[Double]] = Var(None)

  def update(nx: Double, ny: Double): Unit = {
    _x() = Some(nx)
    _y() = Some(ny)
  }

  override def x: Rx[Option[Double]] = _x

  override def y: Rx[Option[Double]] = _y
}
