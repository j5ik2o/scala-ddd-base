package com.github.j5ik2o.dddbase.memcached
import cats.data.ReaderT
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => ReaderT[Task, MemcachedConnection, AggregateType]

}
