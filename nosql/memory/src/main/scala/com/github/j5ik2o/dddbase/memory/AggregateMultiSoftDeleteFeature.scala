package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO

trait AggregateMultiSoftDeleteFeature extends AggregateMultiSoftDeletable[RIO] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[RIO] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): RIO[Long] = dao.softDeleteMulti(ids.map(_.value.toString))

}
