# Notes

Different between the distributed vs parallel
* Split data over several nodes (Shared memory)
* Node (Worker/Thread) processes independently on data shards in parallel
* Combine when done - optional
* Need to worry about network latency
* Chart Important Latency Numbers
* Apache Spark has distributed data parallel model called RDDs (Resilient Distributed Datasets)
* `@transient lazy val` prevent serialized


Spark write in memory and limit networking transfer while hadoop is use disks for writing intermediate data => 100x faster (both has fail tolerance)
## Key Ideas
Introduce 2 important concerns had to worry about
* Partial failure
* Latency (cannot be masked - forgot about) => reduce amount of its

## Resilient Distributed Datasets (RDDs) Spark Distributed Abstraction
* Immutable sequential 
* Make heavy use of higher-order functions
* Some common APIS same cross 2 data model
    * map
    * flatMap
    * filter
    * reduce
    * fold
    * aggregate
* Create RDDs
    * Transforming an existing RDD
    * From a SparkContext or SparkSession object
        * Create Instance of its first => pass config 
        * `parallelize` : Create RDD out of regular scala collection 
        * `textfile` : Read textfile => Return RDD stream 
## Transformations and Actions - Operations
* Scala 
    * Transformer: Return new collections as results
    * Accessors : Return single values as results
* Spark
    * Transformer: new RDDs as results => Lazy => Deal with network  (Staged up computation)
        * map, flatMap, filter, distinct
        * union, intersection, subtract, ca  rtesian
    * Actions: Compute result from RDD and either returned (value - not RDD) or saved to external storage system (HDFS) => Eager
        * collect, count, take, reduce, foreach
        * takeSample, takeOrdered, saveAsTextFile, saveAsSequenceFile
    * Reduce network communication required to undertake Spark jobs
    * Need to  invoke some kind of actions to start off the computation

## Evaluation in Spark
* Allow control what is cached in memory
    * `persist()` => need to pass storage level would like to persist
    * `cache()` => default storage level => shorthand => regular java object

* Other ways to persist dataset 
    * In memory as regular Java objects (cache)
    * on disk as regular Java objects 
    * in memory as serialized Java Object (more compact)
    * on disk as serialized Java objects (more compact)
    * both in memory and on disk (spill over to disk to avoid re-computation)

| Level | Space used | CPU time | In Memory | On disk
|---|---|---|---|---|
|MEMORY_ONLY | High | Low | Y | N |
|MEMORY_ONLY_SER | Low | High | Y | N |
|MEMORY_AND_DISK | High | Medium | Some | Some |
|MEMORY_AND_DISK_SER | Low | High | Some | Some |
|DISK_ONLY | Low | High | N | Y |

* Lazy can fuse and check operations in queue to optimize

## Cluster Topology
* Organized in a master worker topology
* master => driver program
    * SparkContext
    * main method runs
* worker => worker node
    * Executor
    * Store data and cache
* Communicate via cluster manager (Yarn - Mesos)
    * Managing Resources 
    * Scheduling

## Reduction Operations
* In RDDs Collection `foldLeft` and `foldRight` do not exist => `aggregate` => transform acc to new type => project down to more simpler data type
* Enforcing ordering and try to run seq across cluster nodes is sometimes impossible and waste computation power => synchronize nodes => network => extremely costly

## Pair RDDs - Distributed Key-Value Pairs
* Do operation by keys
* Group data by keys
* To create => regular RDD => map to tuple2
### Transformations
* `groupByGKey`
* `reduceByKey`
* `mapValues`
* `keys`
* `join`
* `leftOuterJoin`/ `rightOuterJoin`
### Action
* `countByKey`

