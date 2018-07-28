package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateMultiSoftDeleteFeature extends AggregateMultiSoftDeletable[RIO] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[RIO] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): Task[Long] =
    Task.deferFutureAction { implicit ec =>
      import profile.api._
      db.run(dao.filter(_.id.inSet(ids.map(_.value).toSet)).map(_.status).update(DELETE)).map(_.toLong)
    }

}
