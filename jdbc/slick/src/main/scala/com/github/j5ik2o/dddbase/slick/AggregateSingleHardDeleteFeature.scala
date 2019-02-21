package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleWriter }
import monix.eval.Task

trait AggregateSingleHardDeleteFeature extends AggregateSingleHardDeletable[Task] with AggregateBaseWriteFeature {
  this: AggregateSingleWriter[Task] =>

  override def hardDelete(id: IdType): Task[Long] = Task.deferFutureAction { implicit ec =>
    import profile.api._
    db.run(dao.filter(byCondition(id)).delete).map(_.toLong)
  }

}
