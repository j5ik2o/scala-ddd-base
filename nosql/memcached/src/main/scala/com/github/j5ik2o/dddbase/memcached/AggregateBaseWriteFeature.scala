package com.github.j5ik2o.dddbase.memcached
import cats.data.ReaderT
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => ReaderT[Task, MemcachedConnection, RecordType]

}
