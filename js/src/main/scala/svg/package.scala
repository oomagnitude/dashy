import org.scalajs.dom.raw.SVGElement

package object svg {
  val transform = Transform
  val svgTag = Svg

  implicit class SvgElementsExt[E <: SVGElement, T](override protected val elements: Seq[(E, T)]) extends SvgOps[E, T]

  implicit class SvgElementExt[E <: SVGElement](element: E) {
    def selection[T](datum: T): SvgOps[E, T] = new SvgOps[E, T] {
      override protected def elements: Seq[(E, T)] = IndexedSeq((element, datum))
    }
  }
}
