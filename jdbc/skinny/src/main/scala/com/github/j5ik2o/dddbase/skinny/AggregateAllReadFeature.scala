package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateAllReader
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateAllReadFeature extends AggregateAllReader[RIO] with AggregateBaseReadFeature {

  override def resolveAll: RIO[Seq[AggregateType]] = ReaderT[Task, DBSession, Seq[AggregateType]] {
    implicit dbSession: DBSession =>
      for {
        results <- Task {
          dao.findAll()
        }
        aggregates <- Task.sequence(results.map(convertToAggregate(_)(dbSession)))
      } yield aggregates
  }

}
