package com.github.j5ik2o.dddbase.redis
import cats.data.ReaderT
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => ReaderT[Task, RedisConnection, AggregateType]

}
