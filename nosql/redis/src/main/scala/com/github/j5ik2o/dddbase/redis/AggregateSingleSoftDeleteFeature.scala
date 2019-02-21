package com.github.j5ik2o.dddbase.redis

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateSingleSoftDeleteFeature
    extends AggregateSingleSoftDeletable[ReaderT[Task, RedisConnection, ?]]
    with AggregateBaseReadFeature {
  override type RecordType <: RedisDaoSupport#SoftDeletableRecord
  override type DaoType <: RedisDaoSupport#Dao[ReaderT[Task, RedisConnection, ?], RecordType] with RedisDaoSupport#DaoSoftDeletable[
    ReaderT[Task, RedisConnection, ?],
    RecordType
  ]

  override def softDelete(id: IdType): ReaderT[Task, RedisConnection, Long] = ReaderT { con =>
    dao.softDelete(id.value.toString).run(con)
  }

}
