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

## Shuffling
* is moving data on the network
* happens transparently as part of operations like `groupByKey` => sent to main machine with their responding keys 
* shuffling is bad => do as little as possible cuz these order of magnitude
* use reduceByKey first (much more example)
    * reduce data on the mapper side first
    * reduce the data that sent over the network during the shuffle

## Partitioning
* Data within RDD is split into many partitions (rigid) => move all around the cluster => do as fairly as possible
* Never span in multiple machine
* Each node contains at least 1 partition
* Number can be configured
* To Customize => only works on Pair RDDs => done based on key 
* result should always be persisted
Has 2 types:
* Hash (default)
* Range

### Hash
* Compute the partition p for every tuple in the pair RDD `p.hashCode % numberPartitions`
### Range
* Key has predefined ordering (Int, Char, String) => may be more efficient
* Set of sorted ranges of keys

### Customization
* Explicitly call`partitionBy` on RDD and pass arguments (instance of `Partitioner`)
    * `RangePartitioner(num, rddref)` => create instance => Pass to `partitionBy` method on rdd 
* Keep track of transformation use in RDD (cuz some certain transformation uses certain type of partitioner)
    * pass from parent rdd (the results of transformation is partitioned)
    * Other transformation provide a new transformation 
        * cogroup
        * groupWith
        * join/leftOuterJoin/rightOuterJoin
        * groupByKey/reduceByKey/foldByKey/combineByKey
        * partitionBy
        * sort
        * itersection
        * distinct
        * repartition
        * coalesce
        * mapValues/flatMapValues (parent)
        * filter (parent)
        * Other => del the partitioner

### Optimizing w/ partitioner
* Shuffle occurs when result RDD is depeneded on the same RDD or other
* Type ShuffedRDD
* or use `toDebugString` method
* Avoid network => groupByKey => need to be partitioned first
* Join => 2 RDD => partition using  the same partitioner => cache

## Wide vs Narrow Dependencies
* Lineages => Group of computations in RDD => DAG (Directed Acyclic Graph)
    * Functional => Immutable
    * Keep around function and RDD  => Can recompute => Fail tolerance

* RDD => made up of partitions => atomic pieces dataset
    * dependencies : models relationship betweem RDD ans its partitions with the RDDs was derived from
    * functions => conpute
    * metadata: partition scheme and data placement

* RDD dependencies must encode when data move across the network
* transformation cause shuffle => 2 kinds of dep

 * Wide many parent => Slow => data need to be shuffle
 * Narrow 1 parent => No shuffle necessary => Pipeline optimization is possible
 * `dependencies` methods on RDD => sequence of dependencies objects => used in scheduler
 * `toDebugString` visualization of DAG + info related to scheduling => Grouping call stages
### Narrow dependencies object
* OneToOneDependency
* Prune/Range Dependency
### Wide dependencies object
* ShuffleDependecy

