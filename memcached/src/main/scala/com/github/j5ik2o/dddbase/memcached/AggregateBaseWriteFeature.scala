package com.github.j5ik2o.dddbase.memcached

import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => RIO[RecordType]

}
