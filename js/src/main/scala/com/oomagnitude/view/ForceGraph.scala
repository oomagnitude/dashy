package com.oomagnitude.view

import com.oomagnitude.api.DataPoints
import com.oomagnitude.css.{Styles => css}
import com.oomagnitude.metrics.model.Metrics.{CellInfo, MutualInfo, MutualInfos}
import com.oomagnitude.model.ChartData
import com.oomagnitude.rx.Rxs._
import d3.all._
import jquery.JQueryExt._
import org.scalajs.dom
import org.scalajs.jquery._
import rx._
import svg.Svg
import viz.layout.JsLink

import scala.scalajs.js.Dynamic.literal
import scalacss.ScalatagsCss._
import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}

object ForceGraph {

  def mutualInfoToGraph(dataPoints: DataPoints[MutualInfos]): (IndexedSeq[CellInfo], IndexedSeq[MutualInfo]) = {
    val mis = dataPoints.head._2.value
    (mis.cells.toIndexedSeq, mis.links.toIndexedSeq)
  }

  def mutualInfoLinks(nodes: IndexedSeq[CellInfo], links: IndexedSeq[MutualInfo]): IndexedSeq[(Int, Int)] = {
    val cellIdToIndex = nodes.zipWithIndex.map {
      case (cellInfo, index) => cellInfo.id -> index
    }.toMap

    links.map {
      mutualInfo => (cellIdToIndex(mutualInfo.cells._1), cellIdToIndex(mutualInfo.cells._2))
    }
  }

  def apply[T, N, L](data: ChartData[T], aspectRatio: Double,
                     linkDistance: (Double, Double) => L => Double = {(w: Double, h: Double) => {_: L => 20.0}},
                     nodeStyle: Iterable[N] => N => Seq[JsDom.Modifier] = {ns: Iterable[N] => n: N => List.empty},
                     linkStyle: Iterable[L] => L => Seq[JsDom.Modifier] = {ls: Iterable[L] => l: L => List.empty})(
        implicit convert: DataPoints[T] => (IndexedSeq[N], IndexedSeq[L]),
          toLinks: (IndexedSeq[N], IndexedSeq[L]) => IndexedSeq[(Int, Int)]) = {

    val (width, height) = Svg.dimensions(aspectRatio)
    val distance = linkDistance(width, height)

    val force = d3.layout.force
      .charge(-120)
      .size(width, height)

    val lines = Var(List.empty[dom.Element])
    val circles = Var(List.empty[dom.Element])
    val svg = Svg(aspectRatio)(css.greyBackground, lines.asFrags, circles.asFrags).render

    Obs(data.signal, skipInitial = true) {
      val (nodes, links) = convert(data.signal())
      val nodeToStyle = nodeStyle(nodes)
      val linkToStyle = linkStyle(links)

      force
        .linkDistance({(_: JsLink, index: Int) => distance(links(index))})
        .init(nodes.size, toLinks(nodes, links))
        .start()

      // Update lines first so that they are appended before circles (and thus appear underneath)
      lines() = force.lines.zipWithIndex.map {
        case (line, index) =>
          st.line(linkToStyle(links(index))).render
            .bind(sa.x1, line.source.x)
            .bind(sa.y1, line.source.y)
            .bind(sa.x2, line.target.x)
            .bind(sa.y2, line.target.y)
      }.toList

      circles() = force.points.zipWithIndex.map {
        case (point, index) =>
          val circle = st.circle(sa.r := 5, nodeToStyle(nodes(index))).render
            .bind(sa.cx, point.x)
            .bind(sa.cy, point.y)

          jQuery(circle).tooltip(literal(container = "body", placement = "right"))
          circle
      }.toList

      force.drag(circles().toIndexedSeq)
    }

    svg
  }
}
