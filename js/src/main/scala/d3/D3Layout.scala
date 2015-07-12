package d3

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.JSConverters._

object D3Layout extends Layout {
  /**
   * apply Holten's hierarchical bundling algorithm to edges.
   * [[https://github.com/mbostock/d3/wiki/Bundle-Layout]]
   *
   */
  override def bundle: BundleLayout = ???

  /**
   * derive a custom hierarchical layout implementation
   * [[https://github.com/mbostock/d3/wiki/Hierarchy-Layout]]
   */
  override def hierarchy: HierarchyLayout = ???

  /**
   * produce a hierarchical layout using recursive circle-packing
   * [[https://github.com/mbostock/d3/wiki/Pack-Layout]]
   */
  override def pack: PackLayout = ???

  /**
   * cluster entities into a dendrogram
   * [[https://github.com/mbostock/d3/wiki/Cluster-Layout]]
   * @return
   */
  override def cluster: ClusterLayout = ???

  /**
   * produce a chord diagram from a matrix of relationships.
   * [[https://github.com/mbostock/d3/wiki/Chord-Layout]]
   */
  override def chord: ChordLayout = ???

  /**
   * compute the baseline for each series in a stacked bar or area chart.
   * [[https://github.com/mbostock/d3/wiki/Stack-Layout]]
   */
  override def stack: StackLayout = ???

  /**
   * position a tree of nodes tidily.
   * [[https://github.com/mbostock/d3/wiki/Tree-Layout]]
   */
  override def tree: TreeLayout = ???

  /**
   * compute the start and end angles for arcs in a pie or donut chart
   * [[https://github.com/mbostock/d3/wiki/Pie-Layout]]
   */
  override def pie: PieLayout = ???

  /**
   * use recursive spatial subdivision to display a tree of nodes.
   * [[https://github.com/mbostock/d3/wiki/Treemap-Layout]]
   */
  override def treemap: TreemapLayout = ???

  /**
   * position linked nodes using physical simulation
   * [[https://github.com/mbostock/d3/wiki/Force-Layout]]
   * @return
   */
  override def force: ForceLayout = new D3ForceLayout

  /**
   * recursively partition a node tree into a sunburst or icicle.
   * [[https://github.com/mbostock/d3/wiki/Partition-Layout]]
   */
  override def partition: PartitionLayout = ???

  /**
   * compute the distribution of data using quantized bins.
   * [[https://github.com/mbostock/d3/wiki/Histogram-Layout]]
   */
  override def histogram: HistogramLayout = ???
}

class D3ForceLayout extends ForceLayout {
  private[this] val layout: js.Dynamic = d3.layout.force()

  private[this] var _points = IndexedSeq.empty[Point]
  private[this] var _lines = IndexedSeq.empty[Line]
  private[this] var jsNodes = js.Array[js.Object with js.Dynamic]()
  private[this] var jsLinks = js.Array[js.Object with js.Dynamic]()

  // 1. bind position of nodes and links to Var(Option[Double]), using array index as the "ID"
  override def init(numNodes: Int, linkIndexes: IndexedSeq[(Int, Int)]): ForceLayout = {
    require(numNodes > 0, s"number of nodes ($numNodes) must be a positive number")
    require(linkIndexes.forall { case (i, j) => i >= 0 && i < numNodes && j >= 0 && j < numNodes},
      s"link indexes must be between 0 and $numNodes ($linkIndexes)")

    _points = (0 until numNodes).toIndexedSeq.map(_ => new Point())
    jsNodes = (0 until numNodes).map(i => literal(index = i)).toJSArray
    _lines = linkIndexes.map{case (source, target) => new Line(_points(source), _points(target))}
    jsLinks = linkIndexes.map{case (source, target) => literal(source = source, target = target)}.toJSArray
    layout.nodes(jsNodes).links(jsLinks)
    
    layout.on("tick", { (event: js.Dynamic) =>
      _points.zipWithIndex.foreach {
        case (point, index) =>
          val node = jsNodes(index).asInstanceOf[JsNode]
          point.update(node.x, node.y)
      }
    })
    this
  }

  override def points: IndexedSeq[Point] = _points

  override def lines: IndexedSeq[Line] = _lines

  // 3. set drag force on a given list of nodes, where length of nodes and position matches data internally
  override def drag(elements: IndexedSeq[dom.Node]): ForceLayout = {
    elements.zipWithIndex.foreach {
      case (node, index) =>
        d3.select(node).datum(jsNodes(index)).call(layout.drag)
    }
    this
  }

  override def linkDistance(distance: (JsLink, Int) => Double): ForceLayout = {
    layout.linkDistance({
      (l: js.Dynamic, index: Int) =>
        val link = jsLinks(index).asInstanceOf[JsLink]
        distance(link, index)
    })
    this
  }

  override def charge(charge: Double): ForceLayout = {
    layout.charge(charge)
    this
  }

  override def friction: Double = ???

  override def friction(friction: Double): ForceLayout = ???

  override def alpha(alpha: Double): ForceLayout = ???

  override def gravity(gravity: Double): ForceLayout = ???

  override def linkStrength(strength: Double): ForceLayout = ???

  override def linkStrength: Double = ???

  override def size(width: Double, height: Double): ForceLayout = {
    layout.size(js.Array(width, height))
    this
  }

  override def size: (Double, Double) = ???

  override def theta(theta: Double): ForceLayout = ???

  override def linkDistance(distance: Double): ForceLayout = ???

  override def linkDistance: Double = layout.linkDistance().asInstanceOf[Double]

  override def start(): Unit = layout.start()

}