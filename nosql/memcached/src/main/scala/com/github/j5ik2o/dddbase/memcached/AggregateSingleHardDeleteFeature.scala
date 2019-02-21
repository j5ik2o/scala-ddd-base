package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleWriter }
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateSingleHardDeleteFeature
    extends AggregateSingleHardDeletable[ReaderT[Task, MemcachedConnection, ?]]
    with AggregateBaseWriteFeature {
  this: AggregateSingleWriter[ReaderT[Task, MemcachedConnection, ?]] =>

  override def hardDelete(id: IdType): ReaderT[Task, MemcachedConnection, Long] = dao.delete(id.value.toString)

}
