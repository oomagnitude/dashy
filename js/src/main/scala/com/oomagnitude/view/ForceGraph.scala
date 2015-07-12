package com.oomagnitude.view

import com.oomagnitude.api.DataPoints
import com.oomagnitude.css.{Styles => css}
import com.oomagnitude.metrics.model.Metrics.{CellInfo, MutualInfo, MutualInfos}
import com.oomagnitude.model.ChartData
import com.oomagnitude.rx.Rxs._
import d3.{D3Layout => layout, _}
import jquery.JQueryExt._
import org.scalajs.dom
import org.scalajs.jquery._
import rx._
import svg.Svg

import scalacss.ScalatagsCss._
import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}

object ForceGraph {
  val sTitle = "title".tag[dom.svg.Text]

  case class CellInfoWithId(cellInfo: CellInfo) extends Identifiable[String] {
    override val id: String = cellInfo.id
    val numConnections = cellInfo.numConnections
  }

  case class MutualInfoWithId(mutualInfo: MutualInfo) extends Linkable[String] {
    override val sourceId: String = mutualInfo.cells._1
    override val targetId: String = mutualInfo.cells._2

    val ejc: Double = mutualInfo.ejc
    val jc: Double = mutualInfo.jc
  }

  case class NodesAndLinks[N <: Identifiable[String], L <: Linkable[String]](nodes: IndexedSeq[N], links: IndexedSeq[L])

  def mutualInfoToGraph(dataPoints: DataPoints[MutualInfos]): NodesAndLinks[CellInfoWithId, MutualInfoWithId] = {
    val mis = dataPoints.head._2.value
    NodesAndLinks(mis.cells.toIndexedSeq.map(CellInfoWithId), mis.links.toIndexedSeq.map(MutualInfoWithId))
  }

  def apply[T, N <: Identifiable[String], L <: Linkable[String]](data: ChartData[T], aspectRatio: Double,
                                                                 linkDistance: (Double, Double) => L => Double = {(w: Double, h: Double) => {l: L => 20.0}},
                                                                 nodeStyle: Iterable[N] => N => Seq[JsDom.Modifier] = {ns: Iterable[N] => n: N => List.empty},
                                                                 linkStyle: Iterable[L] => L => Seq[JsDom.Modifier] = {ls: Iterable[L] => l: L => List.empty})
                                                            (implicit convert: DataPoints[T] => NodesAndLinks[N, L]) = {
    val (width, height) = Svg.dimensions(aspectRatio)
    val distance = linkDistance(width, height)

    val force = layout.force[N, L]
      .charge(-120)
      .size(width, height)
      .linkDistance(distance)

    val circles = Var(List.empty[(String, dom.Element)])
    val circleElems = Rx{circles().map(_._2)}
    val lines = Var(List.empty[(String, String, dom.Element)])
    val lineElems = Rx{lines().map(_._3)}
    val svg = Svg(aspectRatio)(css.greyBackground, lineElems.asFrags, circleElems.asFrags).render

    Obs(data.signal, skipInitial = true) {
      val nl: NodesAndLinks[N, L] = data.signal()
      val nodeToStyle = nodeStyle(nl.nodes)
      val linkToStyle = linkStyle(nl.links)

      // Update lines first so that they are appended before circles (and thus appear underneath)
      lines() = nl.links.map(l => (l.sourceId, l.targetId, st.line(linkToStyle(l)).render)).toList
      circles() = nl.nodes.map{ dn =>
        val circle = st.circle(sa.r := 5, nodeToStyle(dn), title:=dn.id).render
        jQuery(circle).tooltip(scalajs.js.Dynamic.literal(container = "body", placement = "right"))
        (dn.id, circle)
      }.toList

      // TODO: construct to bind attributes to signals. Should be Option[T] and remove the attribute if None
      force.data(nl.nodes, nl.links).onTick {
        (g: Graph[N, L], alpha: Double) =>
          circles().foreach {
            case (nodeId, elem) =>
              g.lookup(nodeId).foreach {
                case (graphNode, dataNode) =>
                  elem.setAttribute("cx", graphNode.x.toString)
                  elem.setAttribute("cy", graphNode.y.toString)
              }
          }
          lines().foreach {
            case (sourceId, targetId, elem) =>
              val (source, target) = (g.lookup(sourceId).get._1, g.lookup(targetId).get._1)
              elem.setAttribute("x1", source.x.toString)
              elem.setAttribute("y1", source.y.toString)
              elem.setAttribute("x2", target.x.toString)
              elem.setAttribute("y2", target.y.toString)
          }
      }.start()

      // TODO: construct to bind data and DOM nodes for D3 to work properly
      // Now bind each element to the drag force via the node data
      circles().foreach {
        case (id, elem) =>

          force.node(id).foreach { jsNode =>
            scalajs.js.Dynamic.global.d3.select(elem).datum(jsNode).call(force.drag)
          }
      }
    }

    svg
  }
}
