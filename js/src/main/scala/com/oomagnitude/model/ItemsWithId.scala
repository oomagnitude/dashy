package com.oomagnitude.model

import rx._

trait ItemsWithId[T] {
  private[this] val _itemsWithId = Var(List.empty[(String, T)])

  /**
   * Subscription containing List of pairs in which the first element is the item ID, and the second element is the item
   */
  val itemsWithId: Rx[List[(String, T)]] = _itemsWithId

  val ids = Rx{itemsWithId().map(_._1)}
  val items = Rx{itemsWithId().map(_._2)}

  /**
   * Add a new item to the list. The new item will appear in [[itemsWithId]]
   * @param item the item to add
   * @param id the unique identifier for the item. Default is to generate a UUID.
   * @return the unique identifier for the item
   */
  def add(item: T, id: String = java.util.UUID.randomUUID.toString): String = {
    _itemsWithId() = (id, item) :: _itemsWithId()
    id
  }

  /**
   * Remove an item from the list with the given ID. If the ID doesn't exist, the list will remain unaffected
   *
   * @param id the ID to remove
   * @return the item that was removed, or None if no such item exists
   */
  def remove(id: String): Option[T] = {
    val item = _itemsWithId().find(id == _._1)
    _itemsWithId() = _itemsWithId().filterNot(id == _._1)
    item.map(_._2)
  }

  def clear(): List[(String, T)] = {
    val items = _itemsWithId()
    _itemsWithId() = List.empty
    items
  }
}

class ListWithId[T] extends ItemsWithId[T]
