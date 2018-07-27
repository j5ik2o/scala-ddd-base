package com.github.j5ik2o.dddbase.memcached
import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[RIO] with AggregateBaseReadFeature {
  override type RecordType <: MemcachedDaoSupport#SoftDeletableRecord
  override type DaoType <: MemcachedDaoSupport#Dao[RecordType] with MemcachedDaoSupport#DaoSoftDeletable[RecordType]

  override def softDelete(id: IdType): RIO[Long] = ReaderT { con =>
    dao.softDelete(id.value.toString).run(con)
  }

}
