package d3.facade.selection

import d3.facade.Selectors
import org.scalajs.dom.Element

import scalajs.js

trait Selection extends js.Array[js.Any] with Selectors {
  def attr(name: String): String = js.native
  def attr(name: String, value: js.Any): Selection = js.native
  def attr(name: String, valueFunction: js.Function): Selection = js.native
  def classed(name: String): String = js.native
  def classed(name: String, value: js.Any): Selection = js.native
  def classed(name: String, valueFunction: js.Function2[js.Any, Double, Any]): Selection = js.native
  def classed(classValueMap: Object): Selection = js.native
  def style(name: String): String = js.native
  def style(name: String, value: js.Any, priority: String ): Selection = js.native
  def style(name: String, valueFunction: js.Function, priority: String = js.native): Selection = js.native
  def style(styleValueMap: Object): Selection = js.native
  def property(name: String): Unit = js.native
  def property(name: String, value: js.Any): Selection = js.native
  def property(name: String, valueFunction: js.Function2[js.Any, Double, Any]): Selection = js.native
  def property(propertyValueMap: Object): Selection = js.native
  def text(): String = js.native
  def text(value: js.Any): Selection = js.native
  def text(valueFunction: js.Function2[js.Any, Double, Any]): Selection = js.native
  def html(): String = js.native
  def html(value: js.Any): Selection = js.native
  def html(valueFunction: js.Function2[js.Any, Double, Any]): Selection = js.native
  def append(name: String): Selection = js.native
  var insert: js.Function2[String, String, Selection] = js.native
  var remove: js.Function0[Selection] = js.native
  var empty: js.Function0[Boolean] = js.native
//  def data(values: js.Function2[js.Any, Double, js.Array[js.Any]], key: js.Function2[js.Any, Double, Any] = js.native): UpdateSelection = js.native
  def data(values: js.Array[js.Any], key: js.Function2[js.Any, Double, Any] = js.native): UpdateSelection = js.native
  def data(): js.Array[js.Any] = js.native
  def datum(values: js.Function2[js.Any, Double, Any]): UpdateSelection = js.native
  def datum(): js.Dynamic = js.native
  def filter(filter: js.Function2[js.Any, Double, Boolean], thisArg: js.Any = js.native): UpdateSelection = js.native
  def call(callback: js.Function, args: js.Any*): Selection = js.native
  def each(eachFunction: js.Function2[js.Any, Double, Any]): Selection = js.native
  def on(`type`: String): js.Function2[js.Any, Double, Any] = js.native
  def on(`type`: String, listener: js.Function2[js.Any, Double, Any], capture: Boolean = js.native): Selection = js.native
//  def on(`type`: String, listener: js.ThisFunction2[js.Any, js.Any, Double, js.Any], capture: Boolean = js.native): Selection = js.native
  def size(): Double = js.native
//  def transition(): Transition.Transition = js.native
  def sort[T](comparator: js.Function2[T, T, Double] ): Selection = js.native
  var order: js.Function0[Selection] = js.native
  var node: js.Function = js.native
}

trait UpdateSelection extends Selection {
  def enter(): EnterSelection = js.native
  var update: js.Function0[Selection] = js.native
  var exit: js.Function0[Selection] = js.native
}

trait EnterSelection extends js.Object {
  def append(name: String): Selection = js.native
  var insert: js.Function2[String, String, Selection] = js.native
  var select: js.Function1[String, Selection] = js.native
  var empty: js.Function0[Boolean] = js.native
  var node: js.Function0[Element] = js.native
  var call: js.Function1[js.Function1[EnterSelection, Unit], EnterSelection] = js.native
  var size: js.Function0[Double] = js.native
}