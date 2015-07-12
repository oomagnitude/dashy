package d3

import org.scalajs.dom
import rx._

import scala.scalajs.js

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
  def force: ForceLayout

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

trait JsNode extends js.Object {
  def index: Int = js.native
  def id: String = js.native
  def x: Double = js.native
  def y: Double = js.native
  def px: Double = js.native
  def py: Double = js.native
  def fixed: Option[Boolean] = js.native
  def weight: Int = js.native
}

trait JsLink extends js.Object {
  def source: JsNode = js.native
  def target: JsNode = js.native
}

class Point(val x: Var[Option[Double]], val y: Var[Option[Double]]) {
  def this() = this(Var(None), Var(None))

  def update(nx: Double, ny: Double): Unit = {
    x() = Some(nx); y() = Some(ny)
  }
}

class Line(val source: Point, val target: Point)

/**
 * [[https://github.com/mbostock/d3/wiki/Force-Layout]]
 */
trait ForceLayout {

  // 1. bind position of nodes and links to Var(Option[Double]), using array index as the "ID"
  def init(numNodes: Int, linkIndexes: IndexedSeq[(Int, Int)]): ForceLayout
  
  def points: IndexedSeq[Point]
  def lines: IndexedSeq[Line]

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkDistance]]
   *
   * the function is evaluated for each link (in order), being passed the link and its index, with the
   *                 this context as the force layout; the function's return value is then used to set each link's
   *                 distance. The function is evaluated whenever the layout starts.
   */
  // 2. set link distance using (JsLink, Int) => Double, where Int is the index of the Link object
  def linkDistance(distance: (JsLink, Int) => Double): ForceLayout

  // 3. set drag force on a given list of nodes, where length of nodes and position matches data internally
  def drag(elements: IndexedSeq[dom.Node]): ForceLayout


//  def data(nodes: IndexedSeq[N], links: IndexedSeq[L]): ForceLayout
  def size(width: Double, height: Double): ForceLayout
  def size: (Double, Double)
  def linkStrength(strength: Double): ForceLayout
  def linkStrength: Double
  def friction(friction: Double): ForceLayout
  def friction: Double

  def linkDistance(distance: Double): ForceLayout

//  def linkDistance(distance: L => Double): ForceLayout

  def linkDistance: Double
  def charge(charge: Double): ForceLayout
  def gravity(gravity: Double): ForceLayout
  def theta(theta: Double): ForceLayout
  def alpha(alpha: Double): ForceLayout

//  def onTick(fn: (Graph, Double) => Unit): ForceLayout

  def start(): Unit

//  // TODO: come up with a type for this. It should be a function that takes the current (d3) selection as input and returns Unit. It can also take optional arguments
//  def drag: scalajs.js.Dynamic
//  def node(id: String): Option[scalajs.js.Dynamic]
}

trait HierarchyLayout
trait HistogramLayout
trait PackLayout
trait PartitionLayout
trait PieLayout
trait StackLayout
trait TreeLayout
trait TreemapLayout