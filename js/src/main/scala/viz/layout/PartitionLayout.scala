package viz.layout

case class CoordinateAndExtent(x: Double, y: Double, dx: Double, dy: Double)
trait PartitionLayout extends HierarchyLayout[CoordinateAndExtent] with Sorted with Sized with Value