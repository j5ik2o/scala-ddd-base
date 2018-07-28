package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.AggregateMultiReader
import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateMultiReadFeature extends AggregateMultiReader[RIO] with AggregateBaseReadFeature {

  override def resolveMulti(ids: Seq[IdType]): RIO[Seq[AggregateType]] =
    for {
      results    <- dao.getMulti(ids.map(_.value.toString))
      aggregates <- Task.sequence(results.map(v => convertToAggregate(v)))
    } yield aggregates

}
