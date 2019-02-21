package com.github.j5ik2o.dddbase.redis
import cats.data.ReaderT
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => ReaderT[Task, RedisConnection, RecordType]

}
