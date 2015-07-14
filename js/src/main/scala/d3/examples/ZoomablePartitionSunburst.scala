package d3.examples

import com.oomagnitude.css._
import d3.all._
import org.scalajs.dom.{Event, html}
import svg.{Svg, _}
import viz.layout.{PartitionNode, StufNThings}
import viz.shape.ArcDatum

import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport
import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}

@JSExport
object ZoomablePartitionSunburst {

  @JSExport
  def main(container: html.Div): Unit = {
    val aspectRatio = 1.3714
    val (width, height) = Svg.dimensions(aspectRatio)
    val radius = Math.min(width, height) / 2.0

    val x = d3.scale.linear[Double, Double].range(0, 2 * Math.PI)
    val y = d3.scale.squareRoot.range(0, radius)
    val color = d3.scale.category20c[String]

    implicit val partitionNodeArc = new ArcDatum[PartitionNode] {
      override def outerRadius(d: PartitionNode): Double = Math.max(0, y(d.y + d.dy))
      override def innerRadius(d: PartitionNode): Double = Math.max(0, y(d.y))
      override def startAngle(d: PartitionNode): Double = Math.max(0, Math.min(2 * Math.PI, x(d.x)))
      override def endAngle(d: PartitionNode): Double = Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)))
      override def padAngle(item: PartitionNode): Double = 0.0
    }

    val partition = d3.layout.partition
      .disableSort()
      .value({ _: StufNThings => 1 })

    val arc = d3.shape.arc
    val treeData = new ConcreteTree(JSON.parse(Data.treeJson).asInstanceOf[TestTree])

    // Keep track of the node that is currently being displayed as the root.
    var root = treeData

    val partitionTags = partition.nodes(root).map { n =>
      val path = st.path(sa.d := arc(n), sa.fill := color(if (n.isLeaf) n.parentId.getOrElse("") else n.id) /*, .each(stash)*/).render
      path.onclick = { e: Event =>
        /*    path.transition()
    .duration(1000)
    .attrTween("d", arcTweenZoom(d))*/
      }
      path
    }


    //      d3.selectAll("input").on("change", function change() {
    //    var value = this.value === "count"
    //        ? function() { return 1; }
    //        : function(d) { return d.size; };

    //    path
    //        .data(partition.value(value).nodes)
    //      .transition()
    //        .duration(1000)
    //        .attrTween("d", arcTweenData);
    //  });


    //    d3.select(self.frameElement).style("height", height + "px");

    // Setup for switching data: stash the old values for transition.
    //    function stash(d) {
    //  d.x0 = d.x;
    //  d.dx0 = d.dx;
    //}

    // When switching data: interpolate the arcs in data space.
    //    function arcTweenData(a, i) {
    //  var oi = d3.interpolate({x: a.x0, dx: a.dx0}, a);
    //  function tween(t) {
    //    var b = oi(t);
    //    a.x0 = b.x;
    //    a.dx0 = b.dx;
    //    return arc(b);
    //  }
    //  if (i == 0) {
    //   // If we are on the first arc, adjust the x domain to match the root node
    //   // at the current zoom level. (We only need to do this once.)
    //    var xd = d3.interpolate(x.domain(), [node.x, node.x + node.dx]);
    //    return function(t) {
    //      x.domain(xd(t));
    //      return tween(t);
    //    };
    //  } else {
    //    return tween;
    //  }
    //}

    //    // When zooming: interpolate the scales.
    //    function arcTweenZoom(d) {
    //  var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
    //      yd = d3.interpolate(y.domain(), [d.y, 1]),
    //      yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
    //  return function(d, i) {
    //    return i
    //        ? function(t) { return arc(d); }
    //        : function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
    //  };
    //}

    container.appendChild(svgTag(aspectRatio)(styles.greyBackground,
      st.g(sa.transform := transform.translate(width / 2.0, height / 2.0 + 10), partitionTags)).render)


  }
}
