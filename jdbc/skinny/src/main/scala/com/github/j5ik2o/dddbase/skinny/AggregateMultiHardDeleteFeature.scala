package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateMultiHardDeletable, AggregateMultiWriter }
import monix.eval.Task
import scalikejdbc._

trait AggregateMultiHardDeleteFeature extends AggregateMultiHardDeletable[RIO] with AggregateBaseReadFeature {
  this: AggregateMultiWriter[RIO] with AggregateSingleHardDeleteFeature =>

  override def hardDeleteMulti(ids: Seq[IdType]): RIO[Long] = ReaderT { implicit dbSession =>
    Task {
      dao.deleteBy(sqls.in(dao.defaultAlias.id, ids.map(_.value))).toLong
    }
  }

}
