package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateAllReader
import monix.eval.Task

trait AggregateAllReadFeature extends AggregateAllReader[Task] with AggregateBaseReadFeature {

  import profile.api._

  override def resolveAll: Task[Seq[AggregateType]] =
    for {
      results <- Task.deferFuture {
        db.run(dao.result)
      }
      aggregates <- Task.traverse(results)(convertToAggregate(_))
    } yield aggregates

}
