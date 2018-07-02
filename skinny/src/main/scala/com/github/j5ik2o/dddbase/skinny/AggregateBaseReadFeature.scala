package com.github.j5ik2o.dddbase.skinny

import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate(record: RecordType): RIO[AggregateType]
}
