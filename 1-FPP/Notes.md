# Notes
* Scala recursive function need a defined return type
* Block in scale like let expression in SML
## Evaluation Strategy
* Subtitude Model
* Call by name (Lazy Evaluation)
    * Syntax ` :=> TYPE`
* Call by value (Eager Evaluation)
## Tail recursive
* Only call to current function is optimized 
* If u want to call another function, use `@railrec` anotation (only check)
