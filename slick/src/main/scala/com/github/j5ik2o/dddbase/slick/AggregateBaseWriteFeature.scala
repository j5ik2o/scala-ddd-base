package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => RIO[RecordType]

}
