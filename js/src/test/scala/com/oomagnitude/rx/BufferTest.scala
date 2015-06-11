package com.oomagnitude.rx

import rx._
import utest._

object BufferTest extends TestSuite {
  val tests = TestSuite {
    'repeatedItemsAreBuffered {
      val a = Var(1)
      val buffer = Buffer.create(a)
      a() = 2; a() = 2; a() = 2
      a() = 3; a() = 3

      assert(buffer() == List(3,3,2,2,2))
    }

    'repeatedItemsAreNotBuffered {
      val a = Var(1)
      val b = Rx{a() + 1}
      val buffer = Buffer.create(b)
      a() = 2; a() = 2; a() = 2
      a() = 3; a() = 3

      assert(buffer() == List(4,3))
    }

    'bufferDoesntExceedSize {
      val a = Var(1)
      val buffer = Buffer.create(a, size = Some(10))
      for (i <- 1 to 100) {a() = i}
      assert(buffer() == List(100,99,98,97,96,95,94,93,92,91))
    }
  }
}
