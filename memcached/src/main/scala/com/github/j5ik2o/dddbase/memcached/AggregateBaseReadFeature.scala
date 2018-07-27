package com.github.j5ik2o.dddbase.memcached

import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => RIO[AggregateType]

}
