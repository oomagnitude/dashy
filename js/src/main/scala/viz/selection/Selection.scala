package viz.selection

import org.scalajs.dom.raw.SVGElement

trait Selection[T, E <: SVGElement] {
  def data: IndexedSeq[T]
  def elements: IndexedSeq[E]
  def zip: IndexedSeq[(E, T)]
}
