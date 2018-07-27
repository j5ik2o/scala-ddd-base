package com.github.j5ik2o.dddbase.redis
import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[RIO] with AggregateBaseReadFeature {
  override type RecordType <: RedisDaoSupport#SoftDeletableRecord
  override type DaoType <: RedisDaoSupport#Dao[RecordType] with RedisDaoSupport#DaoSoftDeletable[RecordType]

  override def softDelete(id: IdType): RIO[Long] = ReaderT { con =>
    dao.softDelete(id.value.toString).run(con)
  }

}
