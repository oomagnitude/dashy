package d3


/**
 * [[https://github.com/mbostock/d3/wiki/Layouts]]
 */
trait Layout {
  /**
   * apply Holten's hierarchical bundling algorithm to edges.
   * [[https://github.com/mbostock/d3/wiki/Bundle-Layout]]
   *
   */
  def bundle: BundleLayout

  /**
   * produce a chord diagram from a matrix of relationships.
   * [[https://github.com/mbostock/d3/wiki/Chord-Layout]]
   */
  def chord: ChordLayout

  /**
   * cluster entities into a dendrogram
   * [[https://github.com/mbostock/d3/wiki/Cluster-Layout]]
   * @return
   */
  def cluster: ClusterLayout

  /**
   * position linked nodes using physical simulation
   * [[https://github.com/mbostock/d3/wiki/Force-Layout]]
   * @return
   */
  def force[N <: Identifiable[String], L <: Linkable[String]]: ForceLayout[N, L]

  /**
   * derive a custom hierarchical layout implementation
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout]]
   */
  def hierarchy: HierarchyLayout

  /**
   * compute the distribution of data using quantized bins.
   * [[https://github.com/mbostock/d3/wiki/Histogram-Layout]]
   */
  def histogram: HistogramLayout

  /**
   * produce a hierarchical layout using recursive circle-packing
   * [[https://github.com/mbostock/d3/wiki/Pack-Layout]]
   */
  def pack: PackLayout

  /**
   * recursively partition a node tree into a sunburst or icicle.
   * [[https://github.com/mbostock/d3/wiki/Partition-Layout]]
   */
  def partition: PartitionLayout

  /**
   * compute the start and end angles for arcs in a pie or donut chart
   * [[https://github.com/mbostock/d3/wiki/Pie-Layout]]
   */
  def pie: PieLayout

  /**
   * compute the baseline for each series in a stacked bar or area chart.
   * [[https://github.com/mbostock/d3/wiki/Stack-Layout]]
   */
  def stack: StackLayout

  /**
   * position a tree of nodes tidily.
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout]]
   */
  def tree: TreeLayout

  /**
   * use recursive spatial subdivision to display a tree of nodes.
   * [[https://github.com/mbostock/d3/wiki/Treemap-Layout]]
   */
  def treemap: TreemapLayout
}

trait BundleLayout
trait ChordLayout
trait ClusterLayout

/**
 * [[https://github.com/mbostock/d3/wiki/Force-Layout#nodes]]
 */
case class Node(index: Int, x: Double, y: Double, px: Double, py: Double, fixed: Option[Boolean], weight: Int)
//case class Link(source: Node, target: Node)
//case class ForceTick[N, L](alpha: Double, nodes: IndexedSeq[(Node, N)], links: Iterable[(Link, L)])

trait Graph[N <: Identifiable[String], L <: Linkable[String]] {
  def lookup(id: String): Option[(Node, N)]
  def nodes: IndexedSeq[(Node, N)]
  def links: Iterable[L]
}

trait Identifiable[Id] {
  def id: Id
}

trait Linkable[Id] {
  def sourceId: Id
  def targetId: Id
}

/**
 * [[https://github.com/mbostock/d3/wiki/Force-Layout]]
 */
trait ForceLayout[N <: Identifiable[String], L <: Linkable[String]] {
  
  def data(nodes: IndexedSeq[N], links: IndexedSeq[L]): ForceLayout[N, L]
  def size(width: Double, height: Double): ForceLayout[N, L]
  def size: (Double, Double)
  def linkStrength(strength: Double): ForceLayout[N, L]
  def linkStrength: Double
  def friction(friction: Double): ForceLayout[N, L]
  def friction: Double

  def linkDistance(distance: Double): ForceLayout[N, L]

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkDistance]]
   *
   * the function is evaluated for each link (in order), being passed the link and its index, with the
   *                 this context as the force layout; the function's return value is then used to set each link's
   *                 distance. The function is evaluated whenever the layout starts.
   */
  def linkDistance(distance: L => Double): ForceLayout[N, L]

  def linkDistance: Double
  def charge(charge: Double): ForceLayout[N, L]
  def gravity(gravity: Double): ForceLayout[N, L]
  def theta(theta: Double): ForceLayout[N, L]
  def alpha(alpha: Double): ForceLayout[N, L]

  def onTick(fn: (Graph[N, L], Double) => Unit): ForceLayout[N, L]

  def start(): Unit

  // TODO: come up with a type for this. It should be a function that takes the current (d3) selection as input and returns Unit. It can also take optional arguments
  def drag: scalajs.js.Dynamic
  def node(id: String): Option[scalajs.js.Dynamic]
}

trait HierarchyLayout
trait HistogramLayout
trait PackLayout
trait PartitionLayout
trait PieLayout
trait StackLayout
trait TreeLayout
trait TreemapLayout