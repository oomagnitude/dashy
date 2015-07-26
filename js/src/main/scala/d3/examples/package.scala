package d3

import viz.layout.{Coordinate, LayoutNode, TreeLink}
import scala.language.implicitConversions

package object examples {
  implicit def layoutToCoordinateLink(link: TreeLink[LayoutNode[Coordinate]]): TreeLink[Coordinate] = {
    TreeLink(link.source.layout, link.target.layout)
  }
}
