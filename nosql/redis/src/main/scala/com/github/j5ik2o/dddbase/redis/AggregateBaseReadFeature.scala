package com.github.j5ik2o.dddbase.redis

import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => RIO[AggregateType]

}
