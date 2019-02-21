package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateMultiSoftDeleteFeature
    extends AggregateMultiSoftDeletable[ReaderT[Task, MemcachedConnection, ?]]
    with AggregateBaseReadFeature {
  this: AggregateMultiWriter[ReaderT[Task, MemcachedConnection, ?]] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): ReaderT[Task, MemcachedConnection, Long] =
    dao.softDeleteMulti(ids.map(_.value.toString))

}
