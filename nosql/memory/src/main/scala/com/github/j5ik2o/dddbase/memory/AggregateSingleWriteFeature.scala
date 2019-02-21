package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.AggregateSingleWriter
import monix.eval.Task

trait AggregateSingleWriteFeature extends AggregateSingleWriter[Task] with AggregateBaseWriteFeature {

  override def store(aggregate: AggregateType): Task[Long] = {
    for {
      record <- convertToRecord(aggregate)
      result <- dao.set(record)
    } yield result
  }

}
