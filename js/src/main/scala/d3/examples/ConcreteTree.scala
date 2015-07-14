package d3.examples

import viz.layout.Tree

import scala.scalajs.js.UndefOr

class ConcreteTree(tree: TestTree) extends Tree {
  override def children: UndefOr[Seq[ConcreteTree]] = {
    tree.children.map(_.map(new ConcreteTree(_)))
  }

  override def id: String = tree.name

  override def toString: String = {
    if (children.isDefined) s"ConcreteTree(id = $id, children = ${children.get})"
    else s"ConcreteTree(id = $id)"
  }
}