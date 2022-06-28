# Notes
* `filterNot`
# Translation of For

* `withFilter` is a variant of `filter` that does not produce imtermediate list.

` for x <- e1 yeild e2 ` => `e1.map(x => e2)`

`for x <- 31 if f; s yeild e2` => `for e1 <- with filter (x => f); s yield e2`

`for x <- e1; y <- e2; s yeild e3` => `e1.flatMap(x => for y <- e2; s yeild e3`

As long as client implemented `flatMap`, `map` and `withFilter` we can use for expression


# Functional Random Generators
* Use for testing 
* implement `map` and `flatMap` for using for expression

# Functor
* Like container for type
* has lifted function like `map`
* lifted change content of the container but not the container

# Monad
* Is a parametric type with 2 operations `flatMap` and `unit`
* also call `bind`
* `andThen` like pipe 
* need to satisfy left/right unit law and associative law
* fisher operator :))

# Monoids
* Has empty and combine operations
* Use to combine monad types

# `Try` Type
* create a try not use new cuz `apply` method in companion 
* analogous to Option but catch exception
* case : Success | Failure
* `Try(exprs)`

# [Scalacheck](https://github.com/typelevel/scalacheck/blob/main/doc/UserGuide.md)
* Check properties with random args `extends Properties with ClassOrTraitWantToCheck`
* init object using `lazy val obj : Gen[Obj] = for ... yield`
    * using `arbitrary` `oneOf` `const`
    *  Then give it to scalacheck using `given Arbitrary[TYPE] = Arbitrary(cons)`
* Check properties using `property("desc") = forAll{(given) => true|| false}`
* Can label these complex check properties using `:|` or `|:`

# Structure Induction on Trees
* Prove for each nodes and leaves of the trees

# Lazy List
* `LazyList.cons` `LazyList.empty`
* has `apply` method
* convert to LazyList using `.to` method
* `LazyList.range`
* `#::`
* To pattern check use `LazyList()`

# Lazy Evaluation
* Lazy evaluation
* by-name evaluation
* strict evaluation

# Ordering
* Has abstract method `compare  `
* And other methods like `lt`

# Type Directed Programming
## Implicit Parameter
* `(implicit varName:Type)`
* Will infer value based on type
* Can have only one implicit parameter list, and it must be the last parameter list given
* Caller can be left out but can explicitly pass

### Candidate for Implicit Parameter
* Have Type T
* are marked as implicit
* are visible at the point of function call, or define in companion object with associated with T
* If single (most specific) -> taken as an argument or else throw error
* define `implicit val Name : TYPE =`
    * `Any val`, `lazy val`,`def` or obj def can be marked as implicit
    * implicit function can take type params and implicit params
    ```scala
    implicit def orderingPair[A, B](implicit 
         orderingA: Ordering[A],
         orderingB: Ordering[B]
    ): Ordering[(A, B)] 
    ```
### Searching
* Query file T in lexical scope 
* Continue find its companion object with associated
* Search Type first and then companion object
* If more type match 
    * Check if a type has more fixed parts
    * defined in a class or object which is a subclass defining B (sub class of B)
    * if not error ambiguous report

## Context Bounds

```scala
def printSorted[A: Ordering](as: List[A]): Unit = {
  println(sort(as))
}
```

is a syntactic sugar for
```scala
def printSorted[A](as: List[A])(implicit ev1: Ordering[A]): Unit = {
  println(sort(as))
}
```
`implcitly` to querying inplicit value
