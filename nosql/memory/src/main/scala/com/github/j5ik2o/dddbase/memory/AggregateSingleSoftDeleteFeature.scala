package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import monix.eval.Task

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[Task] with AggregateBaseReadFeature {

  override type RecordType <: MemoryDaoSupport#SoftDeletableRecord
  override type DaoType <: MemoryDaoSupport#Dao[Task, RecordType] with MemoryDaoSupport#DaoSoftDeletable[Task,
                                                                                                         RecordType]

  override def softDelete(id: IdType): Task[Long] =
    dao.softDelete(id.value.toString)

}
