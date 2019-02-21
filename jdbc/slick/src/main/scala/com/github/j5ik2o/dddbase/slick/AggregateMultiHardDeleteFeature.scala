package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.{ AggregateMultiHardDeletable, AggregateMultiWriter }
import monix.eval.Task

trait AggregateMultiHardDeleteFeature extends AggregateMultiHardDeletable[Task] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[Task] with AggregateSingleHardDeleteFeature =>

  override def hardDeleteMulti(ids: Seq[IdType]): Task[Long] = Task.deferFutureAction { implicit ec =>
    import profile.api._
    db.run(dao.filter(byConditions(ids)).delete).map(_.toLong)
  }

}
