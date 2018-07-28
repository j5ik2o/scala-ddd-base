package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleWriter }
import monix.eval.Task

trait AggregateSingleHardDeleteFeature extends AggregateSingleHardDeletable[RIO] with AggregateBaseWriteFeature {
  this: AggregateSingleWriter[RIO] =>

  override def hardDelete(id: IdType): RIO[Long] = Task.deferFutureAction { implicit ec =>
    import profile.api._
    db.run(dao.filter(_.id === id.value).delete).map(_.toLong)
  }

}
