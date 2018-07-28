package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[RIO] with AggregateBaseReadFeature {

  override type RecordType <: MemoryDaoSupport#SoftDeletableRecord
  override type DaoType <: MemoryDaoSupport#Dao[RecordType] with MemoryDaoSupport#DaoSoftDeletable[RecordType]

  override def softDelete(id: IdType): RIO[Long] =
    dao.softDelete(id.value.toString)

}
