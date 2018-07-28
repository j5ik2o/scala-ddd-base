package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateSingleWriter, NullStoreOption, StoreOption }

trait AggregateSingleWriteFeature extends AggregateSingleWriter[RIO] with AggregateBaseWriteFeature {

  override type SO = StoreOption
  override val defaultStoreOption: SO = NullStoreOption

  override def store(aggregate: AggregateType, storeOption: SO): RIO[Long] = {
    for {
      record <- convertToRecord(aggregate)
      result <- dao.set(record)
    } yield result
  }

}
