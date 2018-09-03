package com.github.j5ik2o.dddbase.memory
import com.github.j5ik2o.dddbase.AggregateAllReader
import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateAllReadFeature extends AggregateAllReader[RIO] with AggregateBaseReadFeature {

  override def resolveAll: RIO[Seq[AggregateType]] =
    for {
      results    <- dao.getAll
      aggregates <- Task.sequence(results.map(v => convertToAggregate(v)))
    } yield aggregates

}
