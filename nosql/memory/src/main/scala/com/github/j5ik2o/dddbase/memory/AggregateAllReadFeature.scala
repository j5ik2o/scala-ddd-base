package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.AggregateAllReader
import monix.eval.Task

trait AggregateAllReadFeature extends AggregateAllReader[Task] with AggregateBaseReadFeature {

  override def resolveAll: Task[Seq[AggregateType]] =
    for {
      results    <- dao.getAll
      aggregates <- Task.sequence(results.map(v => convertToAggregate(v)))
    } yield aggregates

}
