package examples.d3

import org.scalajs.dom.html

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

/**
 * [[http://bl.ocks.org/mbostock/4062045]]
 */
@JSExport
object ForceDirectedGraphDynamic {
  val d3 = js.Dynamic.global.d3

  @JSExport
  def main(container: html.Div): Unit = {
    val width = 960
    val height = 500

    val color = d3.scale.category20()

    val force = d3.layout.force()
      .charge(-120)
      .linkDistance(30)
      .size(js.Array(width, height))

    val svg = d3.select(container).append("svg")
      .attr("width", width)
      .attr("height", height)

    val graph = JSON.parse(Data.graphJson)

    force
      .nodes(graph.nodes)
      .links(graph.links)
      .start()

    val link = svg.selectAll(".link")
      .data(graph.links)
      .enter().append("line")
      .attr("class", "link")
      .style("stroke-width", {d: js.Dynamic => math.sqrt(d.value.asInstanceOf[Double])})

    val node = svg.selectAll(".node")
      .data(graph.nodes)
      .enter().append("circle")
      .attr("class", "node")
      .attr("r", 5)
      .style("fill", {d: js.Dynamic => color(d.group)})
      .call(force.drag)

    node.append("title")
      .text({d: js.Dynamic => d.name})

    force.on("tick", {() =>
      link.attr("x1", {d: js.Dynamic => d.source.x})
        .attr("y1", {d: js.Dynamic =>  d.source.y})
        .attr("x2", {d: js.Dynamic => d.target.x})
        .attr("y2", {d: js.Dynamic => d.target.y})

      node.attr("cx", {d: js.Dynamic => d.x})
        .attr("cy", {d: js.Dynamic =>  d.y})
    })
  }
}
