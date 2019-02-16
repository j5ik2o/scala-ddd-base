package com.github.j5ik2o.dddbase.dynamodb
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => RIO[RecordType]

}
