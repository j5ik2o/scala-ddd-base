package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateIO
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

import scala.concurrent.duration.Duration

trait AggregateIOBaseFeature extends AggregateIO[ReaderT[Task, MemcachedConnection, ?]] {
  type RecordType <: MemcachedDaoSupport#Record
  type DaoType <: MemcachedDaoSupport#Dao[ReaderT[Task, MemcachedConnection, ?], RecordType]

  protected val dao: DaoType
  val expireDuration: Duration
}
