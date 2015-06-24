package com.oomagnitude.bind

import com.oomagnitude.bind.ModelViewBinding.CreateElement
import com.oomagnitude.model.ItemsWithId
import com.oomagnitude.view.RxElementGroup

class ViewChannel(view: RxElementGroup) {
  private[this] var bindings = Map.empty[String, ModelViewBinding[_]]

  def bind[T](model: ItemsWithId[T])(implicit createElement: CreateElement[T]): String = {
    val id = java.util.UUID.randomUUID.toString
    val binding = new ModelViewBinding(model, view)(createElement)
    bindings = bindings + (id -> binding)
    id
  }

  def unbind(id: String): Unit = {
    bindings.get(id).foreach(_.unbind())
    bindings = bindings - id
  }
}
