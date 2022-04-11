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
## Evaluation Strategy
* Subtitude Model
* Call by name (Lazy Evaluation)
    * Syntax ` :=> TYPE`
* Call by value (Eager Evaluation)
* Class Subtitude by class params and this
* Methos extension Subtitude for extension params
*
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
