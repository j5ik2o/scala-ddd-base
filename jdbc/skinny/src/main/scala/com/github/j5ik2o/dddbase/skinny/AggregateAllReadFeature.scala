package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateAllReader
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateAllReadFeature extends AggregateAllReader[ReaderT[Task, DBSession, ?]] with AggregateBaseReadFeature {

  override def resolveAll: ReaderT[Task, DBSession, Seq[AggregateType]] = ReaderT[Task, DBSession, Seq[AggregateType]] {
    implicit dbSession: DBSession =>
      for {
        results <- Task {
          dao.findAll()
        }
        aggregates <- Task.gather(results.map(convertToAggregate(_)(dbSession)))
      } yield aggregates
  }

}
