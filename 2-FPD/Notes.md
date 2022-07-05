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
* also have unit method

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
* Search Type first and then companion object (implicit scope)
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
Genelize

```scala 
def f[A: U₁ ... : Uₙ](ps): R = ...
def f[A](ps)(implicit ev₁: U₁[A], ..., evₙ: Uₙ[A]): R = ...
```


`implcitly` to querying inplicit value

# Type Class
* Is a class provide implicit value
* another form of polymorphism 
* resolve the implicit value at compile time
* Can extends the feature of the data types by using companion object with associated implicit value => mo changing in the data type definition

## Laws
* To make other methods which depended by instance of type class can be rely on
* `inverse` : The sign of the result comparing x & y must be inverse the sign of the result of comparing y & x
* `transitive:` x > y & y > z => x > z
* `consistent`: x == y => x sign z then y sign z 
* author should think about such kind of laws and provide the caller the way to check the laws are satisfied

## Implicit def takes implicit params
* Search for implicit params until it terminates
* It also can recursive search if it terminates, if the search results are the same type => return an error
* Can be combine single type params into 1 collection but need to specify them one by one.
* `sortBy` function in collection is a example

## Implicit Conversion
*   `import scala.language.implicitConversions` to write implicit def for implicitConversions
* use it discriminately 
* implicitConversions is a implicit definition take exactly one (non - implicit) parameter

## Implicit class
* to extends methods of a defined class
* covert one type to the other type

```scala
  object Syntax {
    import scala.language.implicitConversions
    implicit class HasSeconds(n: Int) {
      def seconds: Duration = Duration(n, TimeUnit.Second)
    }
  }
```
## Look for implicit conversion
* Type does not conform the expected type
* member is not accessible
* if member did not applicable with the arg
* Implicit conversions can improve the ergonomics of an API but should be used sparingly.

# Contextual Abstraction
* Function and classes can be written without knowing in detail the context in which they will be called or instantiated
* Rather use global variable, monkey patching (change properties in object) or dependency injection - outside of the language and rely on bytecode rewriting -> harder to understand and debug
* Function parameter 
* Implicit parameter `( using name : Type)` deprecated in favor of `implicit`

## Using clause
* Can have multiple parameters/args in `using` clause
* `using` clause can freely mixed with regular parameters ???
* can have several using clause in a row (currying)
* Using clause with most implicit arguments will have more priory
* parameter in `using` clause can be anonymous. Can just write `using Type` if it did not mention and simply passes it as an implicit argument to further methods
* Context bound noted above
## Give instance
* to provide the implicit parameter for `using` clause using `given Name_Opt : Type with`
* if 2 type + anon given instance => Then complier generate same name
* Can query type by using `summon[TYPE]`

## Another notes for searching in implicit and outer scope (companion object)
* Inherited types
* Associated with any type argument in T
* Outer object of inner class 

## Import `Given` Instance
* By name
` import scala.math.Ordering.Int`
* By type
`import scala.math.Ordering.{given Ordering[Int]}`
`import scala.math.Ordering.{given Ordering[?]}` all types
* Bu wildcard
`import scala.math.given` import only given instances
## What does that means one type is more specific to another 
* One type in lexical scope > One type is nested in another
* One type is define in sub class or object which is a class define lesser type
* One type is a generic instance of lesser type
* type A is a subtype of type B

# Type Class
* is a generic class or trait that have companion objects implement it with different types
* Provide another form of polymorphism or ad-hoc polymorphism
* support retroactive extension (can add new feature without changing the class definition)
* Conditional parameter `given name[TYPE]: (using clause) as WraperType wiith`
    * An ordering for lists with elements of type T exists only if there is an ordering for T
    * A class either inherits the traits or it does not. But normal class cannot inherit the traits dependsOn  its parameter type
    * recursive implicit resolve => outer type is constructed first then its implicit parameters are filled in turn.
* Can also have extension methods
    * Type in arg are eligible to call 
# Abstract Algebra with Type Classes
* SemiGroup is a parent of Monoid
* Has associated laws
* Has associative operator `combine`
* Abstract algebra with type classes form natural hierarchy

# Contextual Abstraction - Context Passing
* When passing context around the program make the type u passing is local and specific otherwise crosstalk will appear => ambiguous type
* Passing context when the context is rarely changed otherwise the effects are neligible
* if you use this must not try nested function
* to shield from tampering use `opaque` 
    * like alias but the other type alias will not recognize only in the scope it is defined
    * everywhere the `opaque` type will be treated as abstract type => cannot init
    * syntax `opaque type TYPE = STOCKTYPE`

# Contextual Abstraction - Implicit Function Type
* To remove named using parameter => define a function return implicit param
* Lambda can have implicit param using `?=>` have type implicit function type
    *  have arguments infer like methods
    *  implicit function created on demand => synthesize the argument before the return type type-check
* Implicit parameter in using clause trade type for term => abstract on the caller
* Implicit function type trade types for parameters => abstract on the callee => second degree ctx Abstraction

# Function and State
* Functional is when we substitute any terms in any order => same results => Lambda calculus 
* Stateful => When state change based on its history 
* No state => referential transparency
* Same in mutable state => operational equivalence => if no possible test can distinguish between => find the seq of avtions => replace => same
* for-loop using `do` keyword
* `while do` while loop in scala

# Code Notes
* PartialFunction != Function is that partial is for sub domain and can chain with orElse
    * Call `lift` to call the function
* If and else expression return unit => else only () => then can omit else clause
    * Or we can omit in pattern match clause with empty body

