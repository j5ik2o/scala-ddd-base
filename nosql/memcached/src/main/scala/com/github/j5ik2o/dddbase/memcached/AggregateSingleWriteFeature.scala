package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleWriter
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateSingleWriteFeature
    extends AggregateSingleWriter[ReaderT[Task, MemcachedConnection, ?]]
    with AggregateBaseWriteFeature {

  override def store(aggregate: AggregateType): ReaderT[Task, MemcachedConnection, Long] = {
    for {
      record <- convertToRecord(aggregate)
      result <- dao.set(record, expireDuration)
    } yield result
  }

}
