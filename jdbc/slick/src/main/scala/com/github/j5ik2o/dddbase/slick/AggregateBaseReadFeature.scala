package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import slick.lifted.Rep

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => RIO[AggregateType]

}
