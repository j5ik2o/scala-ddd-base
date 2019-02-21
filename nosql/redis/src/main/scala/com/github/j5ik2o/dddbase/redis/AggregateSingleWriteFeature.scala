package com.github.j5ik2o.dddbase.redis

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleWriter
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateSingleWriteFeature
    extends AggregateSingleWriter[ReaderT[Task, RedisConnection, ?]]
    with AggregateBaseWriteFeature {

  override def store(aggregate: AggregateType): ReaderT[Task, RedisConnection, Long] = {
    for {
      record <- convertToRecord(aggregate)
      result <- dao.set(record, expireDuration)
    } yield result
  }

}
