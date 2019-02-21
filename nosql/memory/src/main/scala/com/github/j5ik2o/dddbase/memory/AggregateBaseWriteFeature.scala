package com.github.j5ik2o.dddbase.memory

import monix.eval.Task

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => Task[RecordType]

}
