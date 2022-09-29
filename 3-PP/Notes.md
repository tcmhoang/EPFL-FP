# Notes
* `:paste` mode in sbt to write multiple lines
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
