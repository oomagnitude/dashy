package d3.layout


import viz.layout._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.JSConverters._
import d3._
import org.scalajs.dom

class D3ForceLayout extends ForceLayout {
   private[this] val layout: js.Dynamic = d3.layout.force()

   private[this] var _points = IndexedSeq.empty[VarPoint]
   private[this] var _lines = IndexedSeq.empty[Line]
   private[this] var jsNodes = js.Array[js.Object with js.Dynamic]()
   private[this] var jsLinks = js.Array[js.Object with js.Dynamic]()

   // 1. bind position of nodes and links to Var(Option[Double]), using array index as the "ID"
   override def init(numNodes: Int, linkIndexes: IndexedSeq[(Int, Int)]): ForceLayout = {
     require(numNodes > 0, s"number of nodes ($numNodes) must be a positive number")
     require(linkIndexes.forall { case (i, j) => i >= 0 && i < numNodes && j >= 0 && j < numNodes},
       s"link indexes must be between 0 and $numNodes ($linkIndexes)")

     _points = (0 until numNodes).toIndexedSeq.map(_ => new VarPoint)
     jsNodes = (0 until numNodes).map(i => literal(index = i)).toJSArray
     _lines = linkIndexes.map{case (source, target) => new Line(_points(source), _points(target))}
     jsLinks = linkIndexes.map{case (source, target) => literal(source = source, target = target)}.toJSArray
     layout.nodes(jsNodes).links(jsLinks)

     layout.on("tick", { (event: js.Dynamic) =>
       _points.zipWithIndex.foreach {
         case (point, index) =>
           val node = jsNodes(index).asInstanceOf[ForceNode]
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

   override def linkDistance(distance: (ForceLink, Int) => Double): ForceLayout = {
     layout.linkDistance({
       (l: js.Dynamic, index: Int) =>
         val link = jsLinks(index).asInstanceOf[ForceLink]
         distance(link, index)
     })
     this
   }

   override def charge(charge: Double): ForceLayout = {
     layout.charge(charge)
     this
   }

   override def friction: Double = layout.friction().asInstanceOf[Double]

   override def friction(friction: Double): ForceLayout = {
     layout.friction(friction)
     this
   }

   override def alpha(alpha: Double): ForceLayout = {
     layout.alpha(alpha)
     this
   }

   override def gravity(gravity: Double): ForceLayout = {
     layout.gravity(gravity)
     this
   }

   override def linkStrength(strength: Double): ForceLayout = {
     layout.linkStrength(strength)
     this
   }

   override def linkStrength: Double = layout.linkStrength().asInstanceOf[Double]

   override def size(width: Double, height: Double): ForceLayout = {
     layout.size(js.Array(width, height))
     this
   }

   override def size: (Double, Double) = {
     val dimensions = layout.size().asInstanceOf[js.Array[js.Dynamic]]
     (dimensions(0).asInstanceOf[Double], dimensions(1).asInstanceOf[Double])
   }

   override def theta(theta: Double): ForceLayout = {
     layout.theta(theta)
     this
   }

   override def linkDistance(distance: Double): ForceLayout = {
     layout.linkDistance(distance)
     this
   }

   override def linkDistance: Double = layout.linkDistance().asInstanceOf[Double]

   override def start(): Unit = layout.start()

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#linkStrength]]
   */
  override def linkStrength(strength: LinkFunction): ForceLayout = {
    layout.linkStrength({
      (l: js.Dynamic, index: Int) =>
        val link = jsLinks(index).asInstanceOf[ForceLink]
        strength(link, index)
    })
    this
  }

  private def on(eventType: String, fn: Double => Unit): Unit = {
    layout.on(eventType, {event: js.Dynamic => fn(event.alpha.asInstanceOf[Double])})
  }

  override def onTick(fn: Double => Unit): ForceLayout = {
    on("tick", fn)
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#stop]]
   */
  override def stop(): Unit = layout.stop()

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#charge]]
   */
  override def charge: Double = layout.charge().asInstanceOf[Double]

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#charge]]
   */
  override def charge(charge: NodeFunction): ForceLayout = {
    layout.charge({
      (n: js.Dynamic, index: Int) =>
        val node = jsNodes(index).asInstanceOf[ForceNode]
        charge(node, index)
    })
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#alpha]]
   */
  override def alpha: Double = layout.alpha().asInstanceOf[Double]

  /**
   * Registers a callback for every "end" event.
   * For more info, see: [[https://github.com/mbostock/d3/wiki/Force-Layout#on]]
   * @param fn function that takes the alpha (cooling) parameter as input and performs a side effect
   */
  override def onEnd(fn: (Double) => Unit): ForceLayout = {
    on("end", fn)
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#gravity]]
   */
  override def gravity: Double = layout.gravity().asInstanceOf[Double]

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#tick]]
   */
  override def tick(): Unit = layout.tick()

  /**
   * Registers a callback for every "start" event.
   * For more info, see: [[https://github.com/mbostock/d3/wiki/Force-Layout#on]]
   * @param fn function that takes the alpha (cooling) parameter as input and performs a side effect
   */
  override def onStart(fn: (Double) => Unit): ForceLayout = {
    on("start", fn)
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#theta]]
   */
  override def theta: Double = layout.theta().asInstanceOf[Double]

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#chargeDistance]]
   */
  override def chargeDistance(maxDistance: Double): ForceLayout = {
    layout.chargeDistance(maxDistance)
    this
  }

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#chargeDistance]]
   */
  override def chargeDistance: Double = layout.chargeDistance().asInstanceOf[Double]

  /**
   * [[https://github.com/mbostock/d3/wiki/Force-Layout#resume]]
   */
  override def resume(): Unit = layout.resume()
}
