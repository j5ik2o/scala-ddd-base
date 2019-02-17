package com.github.j5ik2o.dddbase.dynamodb
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateMultiHardDeletable, AggregateMultiWriter }

trait AggregateMultiHardDeleteFeature extends AggregateMultiHardDeletable[RIO] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[RIO] with AggregateSingleHardDeleteFeature =>

  override def hardDeleteMulti(ids: Seq[IdType]): RIO[Long] = dao.deleteMulti(ids.map(toRecordId))

}
