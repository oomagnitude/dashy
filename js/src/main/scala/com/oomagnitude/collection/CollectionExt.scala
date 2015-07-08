package com.oomagnitude.collection

object CollectionExt {
  implicit class CollectionOps[T: Ordering](ts: Iterable[T]) {
    private[this] val ordering = implicitly[Ordering[T]]

    def minAndMax: (T, T) = {
      require(ts.nonEmpty, s"collection ($ts) must be nonempty")
      ts.tail.foldLeft((ts.head, ts.head)) {
        case ((min, max), item) =>
          (smaller(min, item), bigger(max, item))
      }
    }

    private def bigger(left: T, right: T): T = {
      if (ordering.compare(left, right) < 0) right
      else left
    }

    private def smaller(left: T, right: T): T = {
      if (ordering.compare(left, right) < 0) left
      else right
    }
  }
}
