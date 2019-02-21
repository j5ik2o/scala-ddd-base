package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import monix.eval.Task

trait AggregateMultiSoftDeleteFeature extends AggregateMultiSoftDeletable[Task] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[Task] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): Task[Long] = dao.softDeleteMulti(ids.map(_.value.toString))

}
