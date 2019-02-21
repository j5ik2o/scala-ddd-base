package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.{ AggregateNotFoundException, AggregateSingleReader }
import monix.eval.Task

trait AggregateSingleReadFeature extends AggregateSingleReader[Task] with AggregateBaseReadFeature {

  override def resolveById(id: IdType): Task[AggregateType] = {
    for {
      record <- dao.get(toRecordId(id)).flatMap {
        case Some(v) => Task.pure(v)
        case None    => Task.raiseError(AggregateNotFoundException(id))
      }
      aggregate <- convertToAggregate(record)
    } yield aggregate
  }

}
