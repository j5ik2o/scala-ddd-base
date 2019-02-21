package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateMultiHardDeletable, AggregateMultiWriter }
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateMultiHardDeleteFeature
    extends AggregateMultiHardDeletable[ReaderT[Task, DBSession, ?]]
    with AggregateBaseReadFeature {
  this: AggregateMultiWriter[ReaderT[Task, DBSession, ?]] with AggregateSingleHardDeleteFeature =>

  override def hardDeleteMulti(ids: Seq[IdType]): ReaderT[Task, DBSession, Long] = ReaderT { implicit dbSession =>
    Task {
      dao.deleteBy(byConditions(ids)).toLong
    }
  }

}
