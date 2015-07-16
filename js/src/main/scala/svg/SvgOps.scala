package svg

import d3.transition.D3Transition
import org.scalajs.dom.raw.{Event, SVGElement}
import viz.selection.Selection
import viz.transition.Transition

trait SvgOps[E <: SVGElement, T] {
  self =>

  protected def elements: Seq[(E, T)]

  def onclick(click: (E, T) => Unit): Unit = {
    elements.foreach { case (element, datum) =>
      element.onclick = {_: Event => click(element, datum)}
    }
  }
  def onclick(click: T => Unit): Unit = {
    elements.foreach { case (element, datum) =>
      element.onclick = {_: Event => click(datum)}
    }
  }
  def onclick(click: (E, T, Int) => Unit): Unit = {
    elements.zipWithIndex.foreach { case ((element, datum), index) =>
      element.onclick = {_: Event => click(element, datum, index)}
    }
  }
  def transition: Transition[T] = new D3Transition[T](new Selection[T, E] {
    override val zip: IndexedSeq[(E, T)] = self.elements.toIndexedSeq
    val (elements, data) = zip.unzip
  })
}
