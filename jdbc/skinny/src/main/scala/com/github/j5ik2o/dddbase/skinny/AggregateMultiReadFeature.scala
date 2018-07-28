package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiReader
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateMultiReadFeature extends AggregateMultiReader[RIO] with AggregateBaseReadFeature {

  override def resolveMulti(ids: Seq[IdType]): RIO[Seq[AggregateType]] = ReaderT[Task, DBSession, Seq[AggregateType]] {
    implicit dbSession: DBSession =>
      for {
        results <- Task {
          dao.findAllBy(byConditions(ids))
        }
        aggregates <- Task.sequence(results.map(convertToAggregate(_)(dbSession)))
      } yield aggregates
  }

}
