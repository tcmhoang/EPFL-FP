# Notes
* Scala recursive function need a defined return type
* Block in scale like let expression in SML
* Extended Backus-Naur form (EBNF)
* interpolate string `s"${}"`
* `require(boolean exp, string)` like throwing IllegalArgumentException (enforce preconditions)
* `assert` like `require` but throws AssertionError (check conditions)
* end marker
* select operator is dot notation
* Type Alias `type FunSet = Int => Boolean`
* Trait can be used as multiple inheritance (using linear approach) `With`
* Missing implementation `???`
* `abstract` modifier
* `lazy val` lazy init variable
* package can have mutiple lines in declaration (Which is continue from the previous lines)
* val in parameter to define immutable field
* Function is object
* Anon function is itself a block instantiate a function
* Right Associativity 
* blob `...`
## Evaluation Strategy
* Subtitude Model
* Call by name (Lazy Evaluation)
    * Syntax ` :=> TYPE`
* Call by value (Eager Evaluation)
* Class Subtitude by class params and this
* Methos extension Subtitude for extension params
* Check and Cast `isInstanceOf` `asInstanceOf`

## Tail recursive >< Linear recursion
* Only call to current function is optimized 
* If u want to call another function, use `@railrec` anotation (only check)

## High Order Function
* Can be passed as a parameter and return as a result
* Anonymous function `(v1:t1,v2) => body` is syntactic sugar for using block
## Currying
* Mean do not need params only anonymous function
* Functional Types associate to the right
* Wrap in tuple

## Average Damping
* Newton - average successive values of the original sequence

## Extensions
* Can only add new members not override existing ones
* Cannot refer to other class members via this
* syntax `extension (identifier:TYPE)`

## Relaxed Identifiers
* AlphaNumeric: `Letter + [Letter/Number]+` or `^Letter_operator symbols+$`
* Symbolic: `Operator symbol+`
* `_` is letter

## Infix Notation
* Operator method with a single param can be used as an infix operator
* AlphaNumeric can also be use if it's declared with `infix` modifier

## Precedence Rule (first char)
* All letters
* `|`
* `^`
* `&`
* `<>`
* `= !`
* `:`
* `+ -`
* `* / %`
* other symbols

## MUnit
* `test(string.ignore` ignore test
* `assertEquals` show diff `assert` does not throw `Fail Exception`
* use `trait` to saved common data in each test (Fail create instance wont run the tests)

## Persistence data structure
* Create new data structure based on the old ones.
* Base class of a class is a super class
* `override` modifier is needed to override the base class.

## Object Definition (Singleton)
* Create 1 instance instead using class definition

## Companion Object
* Object can have the same name (Scala has two global namespaces: types, value)
* If a class and object in the same src file have the same name
* Same as static class definition in java

## Program 
* Like java object with main method `def main(args: Array[String]) : Unit = body`
* Or use `@main` annotation (more convenient)

## Package
* src path same as pkg name (not enforce)
* refer by fully qualified name
* import 
 * `{obj1, obj2, ...}` import 2 or more classes
 * `name._` import all (wildcard import)
* auto import from scala, java.lang and scala.Predef (Singleton)

## Trait
* Can have parameters , contain fields and concrete methods
* can be inherited from many traits

## Class hierarchy
* Super: Any -> AnyVal/ AnyRef -> Nothing (no value)
* Any contains `==, !=, equals, hashCode, toString`
* throw Exception return Nothing

## Type Parameter
* Add to definition of class or function and trait using bra ckets `trait TraitName[T]`
* Type Parameter is complier only, before evaluating program  (type erasure) 

## Polymorphism
* Function can applied to many types (Subtype)
* Class can have instances of different types (Subtype, generics)

## Decomposition
* Double Dispatch
* Pattern Matching
    * use `case` class (Create accessors for members)
    * keyword `match` to get access to members follow the sequence of cases `pat => expr`
        * if no pattern matched => `MatchError` will throw
## Pattern 
* Constructor
* Variable (param) - camelCase
* Wildcard `_`
* Constant - Uppercase
* Type test
*  | or

## List
* `::` cons operator
* pattern `List(p1, ... , pn)` == `p1 :: ... :: pn :: Nil`

## Enums
* ADT = Algebraic data types - Pure data definition
* syntactic sugar for `case class`
* case with out parameter can be defined sequently by separating with comma 
* ordinal and values methods
* can define common methods 
* cases pass parameter using enum must use extend keyword
* parameterlized cases do not exist in value property

## SubType Interact w/ Generics
Bounded parameter
* `<:` upper bound - child
* `>;` lower bound - parent
* can mixed but lower bound came first

Covarience - subtyping relationship holds with the type parameter
* `[+T]` covarient
* `[-T] contravarient 
* `[T]` nonvarient
* arg is contra and return is cova
