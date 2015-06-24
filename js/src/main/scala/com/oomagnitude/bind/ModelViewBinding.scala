package com.oomagnitude.bind

import com.oomagnitude.bind.ModelViewBinding.CreateElement
import com.oomagnitude.model.ItemsWithId
import com.oomagnitude.view.RxElementGroup
import org.scalajs.dom.html
import rx._
import scalatags.JsDom.all._
import com.oomagnitude.rx.Rxs._

object ModelViewBinding {
  type CreateElement[T] = (T, () => Unit) => html.Element
}

class ModelViewBinding[T](model: ItemsWithId[T], view: RxElementGroup)(createElement: CreateElement[T]) {
  private[this] var previous = List.empty[String]

  private[this] val obs = Obs(model.itemsWithId) {
    val removed = previous.filterNot(model.ids().contains)
    val added = model.itemsWithId().filterNot(c => previous.contains(c._1))
    removed.foreach(view.remove)
    added.reverse.foreach {
      case (id, item) =>
        val element = createElement(item, {() => model.remove(id)})
        view.add(element, id)
    }
    previous = model.ids()
  }

  lazy val element = div(view.elements.asFrags).render

  def unbind(): Unit = obs.kill()
}
