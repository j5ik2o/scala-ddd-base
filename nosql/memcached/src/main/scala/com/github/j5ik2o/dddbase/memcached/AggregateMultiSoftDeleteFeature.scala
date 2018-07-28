package com.github.j5ik2o.dddbase.memcached

import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO

trait AggregateMultiSoftDeleteFeature extends AggregateMultiSoftDeletable[RIO] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[RIO] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): RIO[Long] = dao.softDeleteMulti(ids.map(_.value.toString))

}
