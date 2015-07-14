package svg

import scala.language.implicitConversions

/**
 * https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/transform
 */
object Transform {
  object transforms {
    def apply(items: String*) = items.mkString(",")
  }

  /**
   * This transform definition specifies a translation by `x` and `y`. This is equivalent to `matrix(1 0 0 1 x y)`. If
   * `y` is not provided, it is assumed to be zero.
   */
  object translate {
    def apply(x: Double, y: Double) = s"translate($x,$y)"

    def apply(x: Int, y: Int) = s"translate($x,$y)"

    def apply(x: Int) = s"translate($x)"
    def apply(x: Double) = s"translate($x)"
  }

  /**
   * This transform definition specifies a rotation by a degrees about a given point. If optional parameters x and y are
   * not supplied, the rotate is about the origin of the current user coordinate system. The operation corresponds to
   * the matrix
   * <pre>
   *  cos(a)  -sin(a) 0
   *  sin(a)  cos(a)  0
   *  0       0       1
   *  </pre>
   *
   *  If optional parameters x and y are supplied, the rotate is about the point `(x, y)`. The operation represents the
   *  equivalent of the following transform definitions list: `translate(x, y)` `rotate(a)` `translate(-x, -y)`.
   */
  object rotate {
    def apply(a: Double, x: Double, y: Double) = s"rotate($a,$x,$y)"
    def apply(a: Int, x: Int, y: Int) = s"rotate($a,$x,$y)"
    def apply(a: Int) = s"rotate($a)"
    def apply(a: Double) = s"rotate($a)"
  }

  /**
   * This transform definition specifies a transformation in the form of a transformation matrix of six values.
   * `matrix(a,b,c,d,e,f)` is equivalent to applying the transformation matrix
   * <pre>
   *   a c e
   *   b d f
   *   0 0 1
   * </pre>
   *
   * which maps coordinates from a new coordinate system into a previous coordinate system by the following matrix
   * equalities:
   * <pre>
   *   x_prevCoordSys     a c e   x_newCoordSys     ax_newCoordSys+cy_newCoordSys+e
   *  (y_prevCoordSys) = (b d f) (y_newCoordSys) = (bx_newCoordSys+dy_newCoordSys+f)
   *        1             0 0 1        1                          1
   * </pre>

   */
  object matrix {
    def apply(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = s"matrix($a,$b,$c,$d,$e,$f)"
  }

  /**
   * This transform definition specifies a scale operation by `x` and `y`. This is equivalent to `matrix(x 0 0 y 0 0)`.
   * If `y` is not provided, it is assumed to be equal to `x`.
   */
  object scale {
    def apply(x: Int, y: Int) = s"scale($x,$y)"
    def apply(x: Int) = s"scale($x)"
  }

  /**
   * This transform definition specifies a skew transformation along the x axis by `a` degrees. The operation corresponds
   * to the matrix
   * <pre>
   *   1 tan(a) 0
   *   0   1    0
   *   0   0    1
   * </pre>
   */
  object skewX {
    def apply(a: Int) = s"skewX($a)"
  }

  /**
   * This transform definition specifies a skew transformation along the y axis by a degrees. The operation corresponds
   * to the matrix
   * <pre>
   *     1    0 0
   *   tan(a) 1 0
   *     0    0 1
   * </pre>
   */
  object skewY {
    def apply(a: Int) = s"skewY($a)"
  }


}
