package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => RIO[RecordType]

}
