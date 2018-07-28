package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateIO, AggregateLongId }
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

import scala.concurrent.duration.Duration

trait AggregateIOBaseFeature extends AggregateIO[RIO] {
  override type IdType <: AggregateLongId
  type RecordType <: MemcachedDaoSupport#Record
  type DaoType <: MemcachedDaoSupport#Dao[RecordType]

  protected val dao: DaoType
  val expireDuration: Duration
}

object AggregateIOBaseFeature {
  type RIO[A] = ReaderT[Task, MemcachedConnection, A]
}
