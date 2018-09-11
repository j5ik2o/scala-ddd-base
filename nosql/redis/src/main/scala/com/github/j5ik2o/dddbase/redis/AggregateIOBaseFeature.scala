package com.github.j5ik2o.dddbase.redis

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateIO
import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

import scala.concurrent.duration.Duration

trait AggregateIOBaseFeature extends AggregateIO[RIO] {
  type RecordType <: RedisDaoSupport#Record
  type DaoType <: RedisDaoSupport#Dao[RecordType]

  protected val dao: DaoType
  val expireDuration: Duration
}

object AggregateIOBaseFeature {
  type RIO[A] = ReaderT[Task, RedisConnection, A]
}
