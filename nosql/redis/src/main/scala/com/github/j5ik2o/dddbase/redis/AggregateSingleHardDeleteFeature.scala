package com.github.j5ik2o.dddbase.redis

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleWriter }
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateSingleHardDeleteFeature
    extends AggregateSingleHardDeletable[ReaderT[Task, RedisConnection, ?]]
    with AggregateBaseWriteFeature {
  this: AggregateSingleWriter[ReaderT[Task, RedisConnection, ?]] =>

  override def hardDelete(id: IdType): ReaderT[Task, RedisConnection, Long] = dao.delete(id.value.toString)

}
