package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.AggregateMultiWriter
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateMultiWriteFeature extends AggregateMultiWriter[RIO] with AggregateBaseWriteFeature {

  override def storeMulti(aggregates: Seq[AggregateType]): RIO[Long] =
    for {
      records <- Task.traverse(aggregates) { aggregate =>
        convertToRecord(aggregate)
      }
      result <- dao.putMulti(records)
    } yield result

}
