package viz.layout

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


trait HistogramLayout
trait PackLayout

trait PieLayout
trait StackLayout

trait TreemapLayout