package reductions

import scala.annotation.*
import org.scalameter.*

object ParallelParenthesesBalancingRunner:

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns := 40,
    Key.exec.maxWarmupRuns := 80,
    Key.exec.benchRuns := 120,
    Key.verbose := false
  ) withWarmer(Warmer.Default())

  def main(args: Array[String]): Unit =
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime")
    println(s"speedup: ${seqtime.value / fjtime.value}")

object ParallelParenthesesBalancing extends ParallelParenthesesBalancingInterface:

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean =
      def aux(parentCount: Int, chars: List[Char]) : Boolean =
        if parentCount < 0 then false
        else chars match
          case c :: cs => c match
            case '(' => aux(parentCount + 1, cs)
            case ')' => aux(parentCount - 1, cs)
            case _: Char => aux(parentCount, cs)
          case Nil => parentCount == 0
      aux(0, chars.toList)
          
        
  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean =

    def traverse(idx: Int, until: Int, arg1: Int, arg2: Int) : Tuple2[Int,Int] = {
      if idx == until then (arg1,arg2)
      else chars(idx) match
        case '(' => traverse(idx + 1, until, arg1 + 1, arg2)
        case ')' => traverse(idx + 1, until, if arg1 == 0 then 0 else arg1 - 1, if arg1 == 0 then arg2 + 1 else arg2)
        case _ => traverse(idx + 1, until, arg1, arg2)
    }

    def reduce(from: Int, until: Int) : (Int,Int) = {
      if until - from <= threshold then traverse(from, until, 0, 0)
      else 
        val mid =  from + (until - from) / 2
        val (t1,t2) = parallel(traverse(from, mid, 0, 0), traverse(mid, until, 0, 0))
        (t1,t2) match
          case ((v1, v2), (v3, v4)) =>
             val tmp = v1 - v4
             if tmp > 0 then (tmp + v3, v2)
             else (v3, v2 - tmp) // tmp is negative (--) == +
    }

    reduce(0, chars.length) == (0,0)

  // For those who want more:
  // Prove that your reduction operator is associative!

