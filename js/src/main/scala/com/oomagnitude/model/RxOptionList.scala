package com.oomagnitude.model

import rx._

class RxOptionList[T](newItems: Rx[Option[T]]) extends ItemsWithId[T] {
  Obs(newItems) {newItems().foreach(t => if (!items().contains(t)) add(t))}
}