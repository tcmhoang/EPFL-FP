# Notes

Different between the distributed vs parallel
* Split data over several nodes (Shared memory)
* Node (Worker/Thread) processes independently on data shards in parallel
* Combine when done - optional
* Need to worry about network latency
* Chart Important Latency Numbers
* Apache Spark has distributed data parallel model called RDDs (Resilient Distributed Datasets)


Spark write in memory and limit networking transfer while hadoop is use disks for writing intermediate data => 100x faster (both has fail tolerance)
## Key Ideas
Introduce 2 important concerns had to worry about
* Partial failure
* Latency (cannot be masked - forgot about) => reduce amount of its
