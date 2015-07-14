package d3.shape

import viz.shape.{Diagonal, Shape}

object D3Shape extends Shape {
  override def diagonal: Diagonal = new D3Diagonal
}
