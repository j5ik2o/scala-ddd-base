package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateMultiHardDeletable, AggregateMultiWriter }
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateMultiHardDeleteFeature
    extends AggregateMultiHardDeletable[ReaderT[Task, MemcachedConnection, ?]]
    with AggregateBaseReadFeature {
  this: AggregateMultiWriter[ReaderT[Task, MemcachedConnection, ?]] with AggregateSingleHardDeleteFeature =>

  override def hardDeleteMulti(ids: Seq[IdType]): ReaderT[Task, MemcachedConnection, Long] =
    dao.deleteMulti(ids.map(_.value.toString))

}
