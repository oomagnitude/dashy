package nvd3

import scala.scalajs.js

trait Chart extends js.Object {

  /**
   * Adjust chart margins to give the x-axis some breathing room.
   *
   * @param m
   * @return
   */
  def margin(m: js.Object): Chart = js.native

  /**
   * Tooltips which show all data points.
   *
   * @param b
   * @return
   */
  def useInteractiveGuideline(b: Boolean): Chart = js.native

  /**
   * how fast do you want the lines to transition?
   *
   * @param d
   * @return
   */
  // TODO: Uncaught TypeError: transitionDuration is not a function
//  def transitionDuration(d: Int): Chart = js.native

  /**
   * Show the legend, allowing users to turn on/off line series.
   *
   * @param b
   * @return
   */
  def showLegend(b: Boolean): Chart = js.native

  def showXAxis(b: Boolean): Chart = js.native

  def showYAxis(b: Boolean): Chart = js.native

  def xAxis: Axis = js.native

  def yAxis: Axis = js.native

  def y1Axis: Axis = js.native

  def y2Axis: Axis = js.native

  def height(h: Int): Chart = js.native

  def width(w: Int): Chart = js.native

  def width(w: String): Chart = js.native

  /**
   * Show tooltips on hover
   *
   * @param b
   * @return
   */
  def tooltips(b: Boolean): Chart = js.native

  def x(fn: js.Function1[js.Dynamic, js.Dynamic]): Chart = js.native

  def y(fn: js.Function1[js.Dynamic, js.Dynamic]): Chart = js.native

  /**
   * Too many bars and not enough room? Try staggering labels.
   *
   * @param b
   * @return
   */
  def staggerLabels(b: Boolean): Chart = js.native

  /**
   * Show bar value next to each bar
   * @param b
   * @return
   */
  def showValues(b: Boolean): Chart = js.native

  /**
   * when true, will display those little distribution lines on the axis
   *
   * @param b
   * @return
   */
  def showDistX(b: Boolean): Chart = js.native

  /**
   * when true, will display those little distribution lines on the axis
   *
   * @param b
   * @return
   */
  def showDistY(b: Boolean): Chart = js.native

  def color(obj: js.Object): Chart = js.native

  def tooltipContent(fn: String => String): Chart = js.native

  def scatter: Scatter = js.native

  /**
   * Let's move the y-axis to the right side
   *
   * @param b
   * @return
   */
  def rightAlignYAxis(b: Boolean): Chart = js.native

  /**
   * Allow user to switch between "Grouped" and "Stacked" mode.
   *
   * @param b
   * @return
   */
  def showControls(b: Boolean): Chart = js.native

  def clipEdge(b: Boolean): Chart = js.native

  /**
   * If 'false', every single x-axis tick label will be rendered.
   *
   * @param b
   * @return
   */
  def reduceXTicks(b: Boolean): Chart = js.native

  /**
   * Angle to rotate x-axis labels.
   *
   * @param angle
   * @return
   */
  def rotateLabels(angle: Int): Chart = js.native

  /**
   * Distance between each group of bars.
   * @param amount
   * @return
   */
  def groupSpacing(amount: Double): Chart = js.native

  def bars: Bars = js.native

  /**
   * Display labels on the chart
   * @param b
   * @return
   */
  def showLabels(b: Boolean): Chart = js.native

  /**
   * Turn on Donut mode. Makes pie chart look tasty!
   *
   * @param b
   * @return
   */
  def donut(b: Boolean): Chart = js.native

  /**
   * Configure how big you want the donut hole size to be
   * @param v
   * @return
   */
  def donutRatio(v: Double): Chart = js.native

  /**
   * Configure what type of data to show in the label (for donut charts)
   *
   * @param t type of label - Can be "key", "value" or "percent"
   * @return
   */
  def labelType(t: String): Chart = js.native

  /**
   * Configure the minimum slice size for labels to show up
   * @param d appears to be a value <1 (example had 0.05)
   * @return
   */
  def labelThreshold(d: Double): Chart = js.native

  /**
   * CSS classes for table (for bootstrap styling)
   * @param c
   * @return
   */
  def tableClass(c: String): Chart = js.native

  /**
   * Configure table columns. Example:
   *
   * [
                  {
                    key: 'key',
                    label: 'Name',
                    showCount: true,
                    width: '75%',
                    type: 'text',
                    classes: function(d) { return d.url ? 'clickable name' : 'name' },
                    click: function(d) {
                       if (d.url) window.location.href = d.url;
                    }
                  },
                  {
                    key: 'type',
                    label: 'Type',
                    width: '25%',
                    type: 'text'
                  }
                ]
   * @param obj
   * @return
   */
  def columns(obj: js.Object): Chart = js.native
}

