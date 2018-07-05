package com.github.j5ik2o.dddbase.skinny

import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => RIO[RecordType]

}
