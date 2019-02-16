package com.github.j5ik2o.dddbase.dynamodb
import com.github.j5ik2o.dddbase.AggregateSingleWriter
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO

trait AggregateSingleWriteFeature extends AggregateSingleWriter[RIO] with AggregateBaseWriteFeature {

  override def store(aggregate: AggregateType): RIO[Long] = {
    for {
      record <- convertToRecord(aggregate)
      result <- dao.put(record)
    } yield result
  }

}
