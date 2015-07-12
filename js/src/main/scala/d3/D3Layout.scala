package d3

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import com.oomagnitude.js._
import js.Dynamic.literal

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
  override def force[N <: Identifiable[String], L <: Linkable[String]]: ForceLayout[N, L] = new D3ForceLayout[N, L]

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

object D3GraphState {
  def empty[N <: Identifiable[String], L <: Linkable[String]] = new D3GraphState[N, L](IndexedSeq.empty, IndexedSeq.empty)
}

class D3GraphState[N <: Identifiable[String], L <: Linkable[String]](val nodeData: IndexedSeq[N], override val links: IndexedSeq[L]) extends Graph[N, L] {
  val nodeIdToIndex = nodeData.zipWithIndex.map { case (n, i) => n.id -> i }.toMap
  val indexToNodeId = nodeIdToIndex.map(_.swap)
  val linkIdToIndex = links.zipWithIndex.map {case (l, i) => linkId(l) -> i}.toMap
  val indexToLinkId = linkIdToIndex.map(_.swap)

  val jsNodes = nodeData.zipWithIndex.map {case (nodeData, index) => literal(index = index, id = nodeData.id)}.toJSArray

  val jsLinks = links.map(
    l =>
      literal(
        source = nodeIdToIndex(l.sourceId),
        target = nodeIdToIndex(l.targetId),
        linkId = linkId(l))
  ).toJSArray

  private def linkId(link: L) = s"${link.sourceId}|${link.targetId}"

  def graphNodes = jsNodes.map { d =>
    val fixed = d.fixed.toOption[Boolean]
    Node(d.index.asInstanceOf[Int], d.x.asInstanceOf[Double], d.y.asInstanceOf[Double],
    d.px.asInstanceOf[Double], d.py.asInstanceOf[Double], fixed, d.weight.asInstanceOf[Int])}.toIndexedSeq

  override def lookup(id: String): Option[(Node, N)] = {
    val index = nodeIdToIndex(id)
    val node = graphNodes(index)
    // TODO: check for out of bounds
    Some((node, nodeData(index)))
  }

  override def nodes: IndexedSeq[(Node, N)] = graphNodes.zip(nodeData)
}

class D3ForceLayout[N <: Identifiable[String], L <: Linkable[String]] extends ForceLayout[N, L] {
  private[this] var state = D3GraphState.empty[N, L]
  private[this] val layout: js.Dynamic = d3.layout.force()

  override def data(nodes: IndexedSeq[N], links: IndexedSeq[L]): ForceLayout[N, L] = {
    state = new D3GraphState[N, L](nodes, links)
    layout.nodes(state.jsNodes).links(state.jsLinks)
    this
  }

  override def onTick(fn: (Graph[N, L], Double) => Unit): ForceLayout[N, L] = {
    layout.on("tick", { (event: js.Dynamic) =>
      // TODO:  An undefined behavior was detected: undefined is not an instance of java.lang.Boolean
      // event.alpha.asInstanceOf[Double]
      fn(state, 0.0)
    })
    this
  }

  override def charge(charge: Double): ForceLayout[N, L] = {
    layout.charge(charge)
    this
  }

  override def friction: Double = ???

  override def friction(friction: Double): ForceLayout[N, L] = ???

  override def alpha(alpha: Double): ForceLayout[N, L] = ???

  override def gravity(gravity: Double): ForceLayout[N, L] = ???

  override def linkStrength(strength: Double): ForceLayout[N, L] = ???

  override def linkStrength: Double = ???

  override def size(width: Double, height: Double): ForceLayout[N, L] = {
    layout.size(js.Array(width, height))
    this
  }

  override def size: (Double, Double) = ???

  override def theta(theta: Double): ForceLayout[N, L] = ???

  override def linkDistance(distance: Double): ForceLayout[N, L] = ???

  override def linkDistance: Double = layout.linkDistance().asInstanceOf[Double]


  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkDistance]]
   *
   * the function is evaluated for each link (in order), being passed the link and its index, with the
   * this context as the force layout; the function's return value is then used to set each link's
   * distance. The function is evaluated whenever the layout starts.
   */
  override def linkDistance(distance: (L) => Double): ForceLayout[N, L] = {
    layout.linkDistance({
      l: js.Dynamic =>
        val link = state.links(state.linkIdToIndex(l.linkId.asInstanceOf[String]))
        distance(link)
    })
    this
  }

  override def start(): Unit = layout.start()

  override def drag: js.Dynamic = layout.drag

  override def node(id: String): Option[js.Dynamic] = {
    val index = state.nodeIdToIndex.get(id)
    index.map(state.jsNodes)
  }
}