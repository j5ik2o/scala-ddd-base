package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.AggregateMultiWriter
import monix.eval.Task

trait AggregateMultiWriteFeature extends AggregateMultiWriter[Task] with AggregateBaseWriteFeature {

  override def storeMulti(aggregates: Seq[AggregateType]): Task[Long] =
    for {
      records <- Task.traverse(aggregates) { aggregate =>
        convertToRecord(aggregate)
      }
      result <- dao.setMulti(records)
    } yield result

}
