package d3.shape

import viz.shape.{Arc, Diagonal, Shape}

object D3Shape extends Shape {
  override def diagonal: Diagonal = new D3Diagonal()

  override def diagonalRadial: Diagonal = new D3Diagonal(d3.d3.svg.diagonal.radial())

  override def arc: Arc = new D3Arc
}
