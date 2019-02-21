package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateMultiReader
import monix.eval.Task

trait AggregateMultiReadFeature extends AggregateMultiReader[Task] with AggregateBaseReadFeature {
  import profile.api._

  override def resolveMulti(ids: Seq[IdType]): Task[Seq[AggregateType]] =
    for {
      results <- Task.deferFuture {
        db.run(dao.filter(byConditions(ids)).result)
      }
      aggregates <- Task.traverse(results)(convertToAggregate(_))
    } yield aggregates
}
