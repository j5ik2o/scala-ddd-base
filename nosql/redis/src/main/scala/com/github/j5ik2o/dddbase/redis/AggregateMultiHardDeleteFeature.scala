package com.github.j5ik2o.dddbase.redis

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateMultiHardDeletable, AggregateMultiWriter }
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateMultiHardDeleteFeature
    extends AggregateMultiHardDeletable[ReaderT[Task, RedisConnection, ?]]
    with AggregateBaseReadFeature {
  this: AggregateMultiWriter[ReaderT[Task, RedisConnection, ?]] with AggregateSingleHardDeleteFeature =>

  override def hardDeleteMulti(ids: Seq[IdType]): ReaderT[Task, RedisConnection, Long] =
    dao.deleteMulti(ids.map(_.value.toString))

}
