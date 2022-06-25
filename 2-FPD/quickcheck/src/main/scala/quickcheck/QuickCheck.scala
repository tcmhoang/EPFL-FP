package quickcheck

import org.scalacheck.*
import Arbitrary.*
import Gen.*
import Prop.forAll

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap:

  lazy val genHeap: Gen[H] = for {
    v <- arbitrary[Int]
    n <- oneOf(const(empty), genHeap)
  } yield insert(v, n)

  given Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if isEmpty(h) then 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("add2") = forAll { (a: Int, b: Int) =>
    val max = if a > b then a else b
    val min = if max == a then b else a
    val h = insert(min, insert(max, empty))
    findMin(h) == min
  }

  property("insert emp del") = forAll { (a: Int) =>
    val h = insert(a, empty)
    isEmpty(deleteMin(h))
  }

  property("Sorted Seq when del min") = forAll { (h: H) =>
    def aux(prev: Int, h: H): Boolean =
      if (isEmpty(h)) then true
      else
        val min = findMin(h)
        if (min < prev) then false
        else
          val nh = deleteMin(h)
          aux(min, nh)
    aux(findMin(h), h)
  }

  property("meld with min") = forAll { (h1: H, h2: H) =>
    findMin(meld(h1, h2)) == Math.min(findMin(h1), findMin(h2))
  }

  property("meld order not change") = forAll { (h1: H, h2: H) =>
    def items(cur: H): List[Int] = {
      if (isEmpty(cur)) Nil
      else {
        findMin(cur) :: items(deleteMin(cur))
      }
    }

    val m1 = meld(h1, h2)
    val min = findMin(h1)
    val m2 = meld(deleteMin(h1), insert(min, h2))
    val items1 = items(m1)
    val items2 = items(m2)
    items1 == items2
  }
