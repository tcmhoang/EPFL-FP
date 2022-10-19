# Notes
* `:paste` mode in sbt to write multiple lines
* `agglomeration` batch together many seq calls, and have a smaller number of parallel tasks
* `scan` method in `List` as apply `fold` to all list prefixes
* `isomorphism` :))
* agnostic
## P vs C
* P - Efficiency - How to divide - Underline hardware
* C - Not at the same time - Modularity + Reponsiveness IO or understandability - When expression start - When and how them exchange info - How to manage to try share resources
### Process
* Instance of a program that is executing in the OS
* Run once or simultaneously
* Time slices => interval switch between different processes => mechanism is multitasking
* intra-processes cannot read or write data directly (isolated)
### Threads
* Can be started within program
* Share same memory
* Has its own Counter and stack
* Communicate => Modify the heap 

#### Create and Start a new thread
* Define a `Thread` subclass => Override the `run` method
* Instantiate a new `Thread` object
* Call `start` method on the `Thread` instance
* To wait its completion call `join` method on the `Thread` instance

## Atomicity
* Either one of the thread execute one of its statements first (not overlap - interleave)
* Operation is atomic if it appears as if it occurred instantaneously from the pov of other threads
* Use synchronized construct 

## Synchronized block
* Code in this block call on the same object is never executed by other threads at the same time
* It use `monitor` inside each instance to ensure this
* At most one thread can own one monitor at any particular time
* `synchronized` method can be invoked on the instance of object (Like a pointer, can instance with type `AnyRef`)
* Composition (Nesting) => Can occur deadlock => Resolving by using ordering 
## Memory Model
* How threads access shared memory
* JVM Rules
    * 2 threads write to seperated locations => do not need for synchronization
    * A thread calls join on another is guaranteed to observed all the writes after it returned
* Notice memory is also a bottle neck
## First Class Task
* `parallel` function
* `Thread` in scala is `task` function 
    * `join` method => obtain value => block
## Asymptotic Analysis
* Work(e) : Number of steps if there no parallel execution
* Depth-Span(e): Number of steps if we have unbounded hardware (Max)
* D + W/Processors

## Amdahl's Law (Performance Evaluation)
* If some computation could not speed up => face up diminishing return (Plateau) 

## Benchmarking 
### Performance factor
* Processor speed
* Number of PO
* Mem Access Latency and Throughput (affects contention)
* Cache behavior (Eg: False sharing, associativity effects)
* Run time Behavior (GC, JIT, thread scheduling)
### Measurement Methodology
* Multiple repetition
* Statistic treatment - Mean - variance - Outliers 
* Ensure steady state (wram-up)
* prevent anomalies (GC, JIT, aggressive optimizations)
### ScalaMeter
* Performance regression testing ( Between run)
* Benchmarking (Current run)
#### Using
* Add dep

```
libraryDependencies += "com.storm-enroute" %% "scalameter-core" % "0.6"
```
* import `import org.scalameter._`
* `measure` method measure time of an expression
* `withWarmer(new Warmer.Default) measure {}` run onlu detect steady state
* `config(key->value)` Eg `Key.exec.min/mapWarmupRuns` `Key.verbose` specify to config parameters
* `withMeasure` methods use with different measure
    * `Measurer.Default` default
    * `IgnoreGC`
    * `OutlierElemination`
    * `MemoryFootprint`
    * `GabageCollectionCycles`
    * methods invocation , boxing count, etc.

### JVM warm-up
* First , Interpreted (Byte code => Soft compoment => Interpreter)
* Some parts are complied to Machine code (Hot parts - Execute more often)
* May apply additional dynamic optimizations (Hot part)
* Read some steady state

## Parallelism and Collections
* Props of collections: Ability to split, combine
* Props of operations: associativity and independence - side effects

### Data Structures
* arrays: imperative
* trees: can be implemented functionally
* locality
* in order to use `fold` as a parallel operation => operation need to be associative | Order must be preserved.

## Associativity
* Order of operands must be reserved
* Order of operation does not matter
* May not be preserved by mapping
* Sub expressions are associative => whole is
* If Expression is commutative + can rotate the arguments => associativity

## Commutativity
* Order of operands does not matter
* make func commutative is easy => pass-in func not

## Scan - Prefix sum
* unsweep + downsweep

## Parallelism
* Task => Distribute execution processes across computing nodes
* Data => Distribute data across computation nodes (Computation details are expressed once)
### Model
* for loop - simplest `range`Type `.par` => convert to parallel range
    * do not return any value => only side effects => correct only seperated mem loc or use some form of synchronization
* fold with all types are the same
* `aggregate` like foldLeft => Monoid => accessors
* transformer like map, filter, flatMap, groupBy 
## Workload
* workload is a function that map input ele to amount work required to processes
* Uniform w(i) = const => easy to parallel
* Irregular w(i) = f(i) => depends on problem instance
## Parallel Collections 
* Scala collections can be convert to parallel collection using `par` method
* to use `fold` as parallel operation => all types need to be the same
### Hierarchy - Seq
* Traversable[T] (foreach)
* Iterable[T] (iterator)
* Seq[T] (an order seq) - index
* Set[T] (no duplicate)
* Map[K,V] - map of keys associated with values (no dup keys)
### Parallel Hierarchy
Traits
* ParIterable[T]
* ParSeq[T]
* ParSet[T]
* ParMap[K,V]
### Agnostic parallel (may or may not be parallel)
* GenIterable[T]
* GenSeq[T]
* GenSet[T]
* GenMap[T]

### Parallel Collections
* ParArray[T] | Array , ArrayBuffer
* ParRange | Range
* ParVector[T] | Vector (List or Seq)
* im/mutable.ParHashSet[T] | im/mutable.HashSet
* im/mutable.ParHashMap[T] | im/mutable.HashMap
* ParTrieMap[K,V] | TrieMap (thread safe with atomic snapshots)
#### Rules
* Avoid mutations to the same memory locations without proper synchronization  
* Or avoid side effects
* Never modify a parallel collection on which parallel operations are in processes
* TrieMap are the exception to above rules
* ConcurrentSkipListSet
*  
## Monoid (F+Neutral ele)
* Associativity
* Neutral element => f => identity function
* Commutativity not matter => when invoke on sequences

## Data Parallel Abstraction
* Iterator
    * `next` + `hasNext` 
    * next can only be call if hasNext return true
    * after hasNext return false => will always return false
* Splitter
    * Counterpart of Iterator use for parallel
    * `split: Seq[Splitter[A]]`
    * `remaining : Int` => estimate on the number of remaining elements
    * after calling `split` the splitter is left in an undefined state ( method like `next`, `hasNext` or `split` cannot be called on splitter)
    * the result splitters traverse disjoint subsets of original splitter (should return at least 2)
    * split must be O(logn) or better => invoke multiple times during execution
* Builder
    * creating new collection
    * `+=` add element to collection
    * `result` return collection contained all the elements which previously added
    * call `result` leave builder undefined state => after that cannot use any more
* Combiner
    * parallel version of the builder 
    * like Builder
    * `combine` => return new combiner that contains elements of input combiners => both input combiners that are left in an undefined state and can no longer be used
    * Must be efficient like Splitter
## BSP - bulk synchronous parallel algorithm
 composed from a sequence of supersteps
* parallel computation, in which processes independently perform local computations and produce some values
* communication, in which processes exchange data
* barrier synchronisation, during which processes wait until every process finishes
