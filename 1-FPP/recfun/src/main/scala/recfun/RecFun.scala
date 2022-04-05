package recfun

import scala.collection.immutable.LazyList.cons

object RecFun extends RecFunInterface:

  def main(args: Array[String]): Unit =
    println("Pascal's Triangle")
    for row <- 0 to 10 do
      for col <- 0 to row do
        print(s"${pascal(col, row)} ")
      println()

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = 
    if c == 0 || c == r
    then 1
    else pascal(c-1 , r -1) + pascal(c, r-1)



  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean =
    def count(parenCount : Int, cs : List[Char]) : Boolean =
      if cs.isEmpty && parenCount == 0 then true
      else if parenCount < 0 then false
      else if !cs.isEmpty then
       val c = cs.head
       if c == '(' then count(parenCount + 1, cs.tail)
       else if c == ')' then count(parenCount - 1, cs.tail)
       else count(parenCount, cs.tail)
      else false

    count(0, chars)




  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = 
    if money == 0 then 1
    else if money > 0 && !coins.isEmpty then  countChange(money - coins.head, coins) + countChange(money, coins.tail)
    else 0