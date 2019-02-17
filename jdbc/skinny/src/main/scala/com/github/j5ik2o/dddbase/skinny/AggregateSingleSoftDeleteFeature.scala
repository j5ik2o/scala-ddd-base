package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc._

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[RIO] with AggregateBaseReadFeature {

  protected final val DELETE = "deleted"

  override def softDelete(id: IdType): RIO[Long] = ReaderT { implicit dbSession =>
    Task {
      dao.updateById(toRecordId(id)).withAttributes('status -> DELETE).toLong
    }
  }

  abstract override protected def byCondition(id: IdType): SQLSyntax =
    super.byCondition(id).and.ne(dao.defaultAlias.status, DELETE)

  abstract override protected def byConditions(ids: Seq[IdType]): SQLSyntax =
    super.byConditions(ids).and.ne(dao.defaultAlias.status, DELETE)
}
