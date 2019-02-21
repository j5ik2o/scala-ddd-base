package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateMultiSoftDeleteFeature
    extends AggregateMultiSoftDeletable[ReaderT[Task, DBSession, ?]]
    with AggregateBaseReadFeature {
  this: AggregateMultiWriter[ReaderT[Task, DBSession, ?]] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): ReaderT[Task, DBSession, Long] = ReaderT { implicit dbDesion =>
    Task {
      dao.updateBy(byConditions(ids)).withAttributes('status -> DELETE).toLong
    }
  }

}
