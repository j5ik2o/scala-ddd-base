package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import monix.eval.Task

trait AggregateMultiSoftDeleteFeature extends AggregateMultiSoftDeletable[Task] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[Task] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): Task[Long] =
    Task.deferFutureAction { implicit ec =>
      import profile.api._
      db.run(dao.filter(byConditions(ids)).map(_.status).update(DELETE)).map(_.toLong)
    }

}
