package d3

import viz.layout.{Tree, StufNThings}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.UndefOr
import scala.scalajs.js.JSConverters._


package object layout {
  private[layout] def extractNode(d: js.Dynamic): StufNThings = {
    StufNThings(d.id.asInstanceOf[String], d.depth.asInstanceOf[Int], parentId(d), childrenIds(d))
  }

  private[layout] def parentId(node: js.Dynamic): Option[String] =
    node.parent.asInstanceOf[UndefOr[js.Dynamic]].map(_.id.asInstanceOf[String]).toOption

  private[layout] def childrenIds(d: js.Dynamic): Seq[String] = {
    d.children.asInstanceOf[UndefOr[js.Dynamic]]
      .map(_.asInstanceOf[js.Array[js.Dynamic]]
        .map(_.id.asInstanceOf[String]).toSeq)
      .getOrElse(Seq.empty)
  }

  private[layout] def computeNodes(layout: js.Dynamic, root: Tree): js.Array[js.Dynamic] =
    layout(convertToLiteral(root)).asInstanceOf[js.Array[js.Dynamic]]

  private[layout] def convertToLiteral(root: Tree): js.Dynamic = {
    val jsObj = literal(id = root.id)
    if (root.children.nonEmpty) {
      val literalChildren = root.children.get.map(convertToLiteral)
      jsObj.children = literalChildren.toJSArray
    }
    jsObj
  }

}
