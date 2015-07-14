package viz.layout

// TODO: to make it possible to run on the JVM, this trait should not rely on scalajs-dom or scalajs
import org.scalajs.dom

import scala.scalajs.js

/**
 * [[https://github.com/mbostock/d3/wiki/Force-Layout#nodes]]
 */

trait ForceNode extends js.Object {
  def index: Int = js.native
  def id: String = js.native
  def x: Double = js.native
  def y: Double = js.native
  def px: Double = js.native
  def py: Double = js.native
  def fixed: Option[Boolean] = js.native
  def weight: Int = js.native
}

trait ForceLink extends js.Object {
  def source: ForceNode = js.native
  def target: ForceNode = js.native
}

/**
 * [[https://github.com/mbostock/d3/wiki/Force-Layout]]
 *
 * The layout preserves ordering of nodes and links, so all data structures returned match the initial ordering.
 * Therefore, indexes for [[IndexedSeq]] can be used to map nodes, links, points and lines to external data structures.
 */
trait ForceLayout {
  type LinkFunction = (ForceLink, Int) => Double
  type NodeFunction = (ForceNode, Int) => Double

  /**
   * Initialize the force layout with a set of nodes and links. After calling this method, each node is bound to a
   * [[Point]], and each link is bound to a [[Line]], each of which contain respective coordinates for nodes and links.
   * The [[Point]] and [[Line]] coordinate are dynamically updated every time the underlying layout changes (i.e., on
   * every tick).
   *
   * @param numNodes the number of nodes to create. The nodes and points will be indexed from 0 to numNodes - 1,
   *                 inclusive.
   * @param linkIndexes a sequence of links, containing a pair of indexes into the nodes sequence, the first being
   *                    the link source and second being the link target. The links and lines will have the same indexes
   *                    as this sequence.
   */
  def init(numNodes: Int, linkIndexes: IndexedSeq[(Int, Int)]): ForceLayout

  /**
   * @return the points in the force layout. Will be empty if `init` has never been called. Indexes correspond to the
   *         ordering given in `init`
   */
  def points: IndexedSeq[Point]

  /**
   * @return the lines in the force layout. Will be empty if `init` has never been called. Indexes correspond to the
   *         ordering given in `init`
   */
  def lines: IndexedSeq[Line]

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#size]]
   */
  def size(width: Double, height: Double): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#size]]
   */
  def size: (Double, Double)

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkDistance]]
   *
   * the function is evaluated for each link (in order), being passed the link and its index, with the
   *                 this context as the force layout; the function's return value is then used to set each link's
   *                 distance. The function is evaluated whenever the layout starts.
   */
  def linkDistance(distance: LinkFunction): ForceLayout

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkDistance]]
   * @param distance between nodes (i.e., the link length), which will be a constant for all links
   */
  def linkDistance(distance: Double): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkDistance]]
   *
   * @return the length of each link. CAUTION: if the layout is configured to use a variable link distance (via a
   *         function), then calling this method may cause a runtime error.
   */
  def linkDistance: Double

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkStrength]]
   */
  def linkStrength(strength: LinkFunction): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkStrength]]
   */
  def linkStrength(strength: Double): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkStrength]]
   */
  def linkStrength: Double

  /**
   * https://github.com/mbostock/d3/wiki/Force-Layout#friction
   */
  def friction(friction: Double): ForceLayout
  /**
   * https://github.com/mbostock/d3/wiki/Force-Layout#friction
   */
  def friction: Double

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#charge]]
   */
  def charge(charge: NodeFunction): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#charge]]
   */
  def charge(charge: Double): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#charge]]
   */
  def charge: Double

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#chargeDistance]]
   */
  def chargeDistance(maxDistance: Double): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#chargeDistance]]
   */
  def chargeDistance: Double

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#theta]]
   */
  def theta(theta: Double): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#theta]]
   */
  def theta: Double

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#gravity]]
   */
  def gravity(gravity: Double): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#gravity]]
   */
  def gravity: Double

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#start]]
   */
  def start(): Unit

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#alpha]]
   */
  def alpha(alpha: Double): ForceLayout
  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#alpha]]
   */
  def alpha: Double

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#resume]]
   */
  def resume(): Unit

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#stop]]
   */
  def stop(): Unit

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#tick]]
   */
  def tick(): Unit

  /**
   * For a given set of DOM elements, enable a drag force. The number of elements should correspond to the number of
   * nodes in the graph, and are expected to be in the corresponding order to the nodes originally constructed for
   * the graph.
   *
   * For more info, see: [[https://github.com/mbostock/d3/wiki/Force-Layout#drag]]
   *
   * @param elements the elements on which to enable the drag force
   */
  def drag(elements: IndexedSeq[dom.Node]): ForceLayout

  /**
   * Registers a callback for every "tick" event.
   * For more info, see: [[https://github.com/mbostock/d3/wiki/Force-Layout#on]]
   * @param fn function that takes the alpha (cooling) parameter as input and performs a side effect
   */
  def onTick(fn: Double => Unit): ForceLayout

  /**
   * Registers a callback for every "start" event.
   * For more info, see: [[https://github.com/mbostock/d3/wiki/Force-Layout#on]]
   * @param fn function that takes the alpha (cooling) parameter as input and performs a side effect
   */
  def onStart(fn: Double => Unit): ForceLayout

  /**
   * Registers a callback for every "end" event.
   * For more info, see: [[https://github.com/mbostock/d3/wiki/Force-Layout#on]]
   * @param fn function that takes the alpha (cooling) parameter as input and performs a side effect
   */
  def onEnd(fn: Double => Unit): ForceLayout
}