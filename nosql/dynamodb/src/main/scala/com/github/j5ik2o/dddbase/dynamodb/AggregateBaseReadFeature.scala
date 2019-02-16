package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => RIO[AggregateType]

}
