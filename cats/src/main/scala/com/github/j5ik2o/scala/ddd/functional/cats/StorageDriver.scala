package com.github.j5ik2o.scala.ddd.functional.cats

import com.github.j5ik2o.scala.ddd.functional.driver.{ AggregateDeletable, AggregateRepository }

trait StorageDriver extends AggregateRepository with AggregateDeletable {
  type RecordType

  protected def convertToRecord(aggregate: AggregateType): RecordType

  protected def convertToAggregate(record: SingleResultType[RecordType]): SingleResultType[AggregateType]
}
