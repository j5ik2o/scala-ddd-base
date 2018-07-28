package com.github.j5ik2o.dddbase.memory
import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => RIO[AggregateType]

}
