package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateMultiHardDeletable, AggregateMultiWriter }
import monix.eval.Task

trait AggregateMultiHardDeleteFeature extends AggregateMultiHardDeletable[RIO] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[RIO] with AggregateSingleHardDeleteFeature =>

  override def hardDeleteMulti(ids: Seq[IdType]): RIO[Long] = Task.deferFutureAction { implicit ec =>
    import profile.api._
    db.run(dao.filter(_.id.inSet(ids.map(_.value))).delete).map(_.toLong)
  }

}
