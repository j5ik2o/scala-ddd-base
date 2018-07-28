package com.github.j5ik2o.dddbase.redis

import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO

import scala.concurrent.duration.Duration

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => RIO[RecordType]

}
