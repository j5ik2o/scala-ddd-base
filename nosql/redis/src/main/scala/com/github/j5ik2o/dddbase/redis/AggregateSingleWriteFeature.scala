package com.github.j5ik2o.dddbase.redis

import com.github.j5ik2o.dddbase.AggregateSingleWriter
import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO

import scala.concurrent.duration.Duration

trait AggregateSingleWriteFeature extends AggregateSingleWriter[RIO] with AggregateBaseWriteFeature {

  override def store(aggregate: AggregateType): RIO[Long] = {
    for {
      record <- convertToRecord(aggregate)
      result <- dao.set(record, expireDuration)
    } yield result
  }

}
