package com.github.j5ik2o.dddbase.slick

import monix.eval.Task

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => Task[AggregateType]

}
