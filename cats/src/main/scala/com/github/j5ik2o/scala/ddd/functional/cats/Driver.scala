package com.github.j5ik2o.scala.ddd.functional.cats

import com.github.j5ik2o.scala.ddd.functional.{ AggregateDeletable, AggregateRepository }

trait Driver extends AggregateRepository with AggregateDeletable {
  type EvalType[A]
  type RealizeType[_]

  type RecordType

  protected def convertToRecord(aggregate: AggregateType): RecordType

  protected def convertToAggregate(record: SingleResultType[RecordType]): SingleResultType[AggregateType]
}
