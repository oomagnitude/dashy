package com.oomagnitude.view

import com.oomagnitude.model.ItemsWithId
import org.scalajs.dom.html
import rx._

class RxElementGroup extends ItemsWithId[html.Element] {
  val elements = Rx{itemsWithId().map(_._2)}
}
