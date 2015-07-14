package viz.layout

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
case class PartitionNode(id: String, depth: Int, x: Double, y: Double, dx: Double, dy: Double, value: Double,
                    childrenIds: Seq[String], parentId: Option[String]) {
  def isLeaf: Boolean = childrenIds.isEmpty
  def isRoot: Boolean = parentId.isEmpty
}

trait PartitionLayout extends HierarchyLayout[PartitionNode] with Sorted with Sized with Value