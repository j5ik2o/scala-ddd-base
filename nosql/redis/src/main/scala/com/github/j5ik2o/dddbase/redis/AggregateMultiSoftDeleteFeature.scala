package com.github.j5ik2o.dddbase.redis

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateMultiSoftDeleteFeature
    extends AggregateMultiSoftDeletable[ReaderT[Task, RedisConnection, ?]]
    with AggregateBaseReadFeature {
  this: AggregateMultiWriter[ReaderT[Task, RedisConnection, ?]] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): ReaderT[Task, RedisConnection, Long] =
    dao.softDeleteMulti(ids.map(_.value.toString))

}
