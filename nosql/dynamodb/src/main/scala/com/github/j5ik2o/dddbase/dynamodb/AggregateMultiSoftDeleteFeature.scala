package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }

trait AggregateMultiSoftDeleteFeature extends AggregateMultiSoftDeletable[RIO] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[RIO] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): RIO[Long] = dao.softDeleteMulti(ids.map(toRecordId))

}
