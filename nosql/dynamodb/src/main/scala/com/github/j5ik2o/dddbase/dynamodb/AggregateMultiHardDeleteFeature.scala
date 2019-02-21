package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.{ AggregateMultiHardDeletable, AggregateMultiWriter }
import monix.eval.Task

trait AggregateMultiHardDeleteFeature extends AggregateMultiHardDeletable[Task] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[Task] with AggregateSingleHardDeleteFeature =>

  override def hardDeleteMulti(ids: Seq[IdType]): Task[Long] = dao.deleteMulti(ids.map(toRecordId))

}
