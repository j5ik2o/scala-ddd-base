package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import monix.eval.Task
import scalikejdbc._

trait AggregateSingleSoftDeleteFeature
    extends AggregateSingleSoftDeletable[ReaderT[Task, DBSession, ?]]
    with AggregateBaseReadFeature {

  protected final val DELETE = "deleted"

  override def softDelete(id: IdType): ReaderT[Task, DBSession, Long] = ReaderT { implicit dbSession =>
    Task {
      dao.updateById(toRecordId(id)).withAttributes('status -> DELETE).toLong
    }
  }

  abstract override protected def byCondition(id: IdType): SQLSyntax =
    super.byCondition(id).and.ne(dao.defaultAlias.status, DELETE)

  abstract override protected def byConditions(ids: Seq[IdType]): SQLSyntax =
    super.byConditions(ids).and.ne(dao.defaultAlias.status, DELETE)
}
