package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateNotFoundException, AggregateSingleReader }
import monix.eval.Task

trait AggregateSingleReadFeature extends AggregateSingleReader[RIO] with AggregateBaseReadFeature {

  override def resolveById(id: IdType): RIO[AggregateType] = {
    for {
      record <- dao.get(id.value.toString).flatMap {
        case Some(v) => Task.pure(v)
        case None    => Task.raiseError(AggregateNotFoundException(id))
      }
      aggregate <- convertToAggregate(record)
    } yield aggregate
  }

}
