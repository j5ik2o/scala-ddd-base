package com.github.j5ik2o.dddbase.memcached

import com.github.j5ik2o.dddbase.AggregateSingleWriter
import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO

import scala.concurrent.duration.Duration

trait AggregateSingleWriteFeature extends AggregateSingleWriter[RIO] with AggregateBaseWriteFeature {

  override type SO = Duration
  override val defaultStoreOption: SO = Duration.Inf

  override def store(aggregate: AggregateType, storeOption: SO): RIO[Long] = {
    for {
      record <- convertToRecord(aggregate)
      result <- dao.set(record, storeOption)
    } yield result
  }

}
