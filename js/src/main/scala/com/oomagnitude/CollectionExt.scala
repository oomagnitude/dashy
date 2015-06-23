package com.oomagnitude

object CollectionExt {
  implicit class CollectionOps[T](ts: Iterable[T]) {
    def minAndMax(implicit toNumber: T => Double): (Double, Double) = {
      require(ts.nonEmpty, s"collection ($ts) must be nonempty")
      val first = toNumber(ts.head)
      ts.tail.foldLeft((first, first)) {
        case ((min, max), item) =>
          val number = toNumber(item)
          (math.min(min, number), math.max(max, number))
      }
    }
  }
}
