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

# Lazy Evaluation
* Lazy evaluation
* by-name evaluation
* strict evaluation

