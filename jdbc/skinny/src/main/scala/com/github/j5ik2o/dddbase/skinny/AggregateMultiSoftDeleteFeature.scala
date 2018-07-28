package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateMultiSoftDeletable, AggregateMultiWriter }
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc._

trait AggregateMultiSoftDeleteFeature extends AggregateMultiSoftDeletable[RIO] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[RIO] with AggregateSingleSoftDeleteFeature =>

  override def softDeleteMulti(ids: Seq[IdType]): RIO[Long] = ReaderT { implicit dbDesion =>
    Task {
      dao.updateBy(sqls.in(dao.defaultAlias.status, ids.map(_.value))).withAttributes('status -> DELETE).toLong
    }
  }

}
