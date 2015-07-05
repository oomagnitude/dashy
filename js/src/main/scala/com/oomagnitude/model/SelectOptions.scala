package com.oomagnitude.model

import com.oomagnitude.view.SelectOption
import rx._

class SelectOptions[T](items: Rx[List[T]], id: T => String, displayName: T => String)(implicit ord: Ordering[SelectOption]) {
  // select list should use this as its source of options
  val selectOptions = Rx {
    items().map(item => SelectOption(id(item), displayName(item))).sorted
  }
  
  // select list should set this id (from the value attribute) when an item is selected
  val selectedId: Var[Option[String]] = Var(None)
  
  // this is the item that was selected and can be used downstream
  private[this] val _selectedItem: Var[Option[T]] = Var(None)
  val selectedItem: Rx[Option[T]] = _selectedItem

  Obs(selectedId, skipInitial = true) {
    val maybeFound = selectedId().flatMap(idText => items().find(id(_) == idText))
    if (maybeFound != _selectedItem()) _selectedItem() = maybeFound
//    _selectedItem() = selectedId().flatMap(idText => items().find(id(_) == idText))
  }

  def clear() = selectedId() = None
}
