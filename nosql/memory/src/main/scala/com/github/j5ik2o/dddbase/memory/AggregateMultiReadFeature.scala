package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.AggregateMultiReader
import monix.eval.Task

trait AggregateMultiReadFeature extends AggregateMultiReader[Task] with AggregateBaseReadFeature {

  override def resolveMulti(ids: Seq[IdType]): Task[Seq[AggregateType]] =
    for {
      results    <- dao.getMulti(ids.map(_.value.toString))
      aggregates <- Task.gather(results.map(v => convertToAggregate(v)))
    } yield aggregates

}
