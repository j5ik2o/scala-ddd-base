package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateSingleSoftDeleteFeature
    extends AggregateSingleSoftDeletable[ReaderT[Task, MemcachedConnection, ?]]
    with AggregateBaseReadFeature {
  override type RecordType <: MemcachedDaoSupport#SoftDeletableRecord
  override type DaoType <: MemcachedDaoSupport#Dao[ReaderT[Task, MemcachedConnection, ?], RecordType] with MemcachedDaoSupport#DaoSoftDeletable[
    ReaderT[Task, MemcachedConnection, ?],
    RecordType
  ]

  override def softDelete(id: IdType): ReaderT[Task, MemcachedConnection, Long] = ReaderT { con =>
    dao.softDelete(id.value.toString).run(con)
  }

}
