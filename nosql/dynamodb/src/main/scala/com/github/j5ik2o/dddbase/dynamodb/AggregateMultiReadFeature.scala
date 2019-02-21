package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.AggregateMultiReader
import monix.eval.Task

trait AggregateMultiReadFeature extends AggregateMultiReader[Task] with AggregateBaseReadFeature {

  override def resolveMulti(ids: Seq[IdType]): Task[Seq[AggregateType]] =
    for {
      results    <- dao.getMulti(ids.map(toRecordId))
      aggregates <- Task.sequence(results.map(v => convertToAggregate(v)))
    } yield aggregates

}
