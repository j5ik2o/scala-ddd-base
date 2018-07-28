package com.github.j5ik2o.dddbase.example.repository

import _root_.slick.jdbc.JdbcProfile
import akka.actor.ActorSystem
import cats.data.ReaderT
import cats.free.Free
import com.github.j5ik2o.dddbase._
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.free.{ UserAccountRepositoryByFree, UserRepositoryDSL }
import com.github.j5ik2o.dddbase.example.repository.memcached.UserAccountRepositoryOnMemcachedWithTask
import com.github.j5ik2o.dddbase.example.repository.memory.UserAccountRepositoryOnMemoryWithTask
import com.github.j5ik2o.dddbase.example.repository.redis.UserAccountRepositoryOnRedisWithTask
import com.github.j5ik2o.dddbase.example.repository.skinny.UserAccountRepositoryBySkinnyWithTask
import com.github.j5ik2o.dddbase.example.repository.slick.UserAccountRepositoryBySlickWithTask
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task
import scalikejdbc.DBSession

import scala.concurrent.duration.Duration

trait UserAccountRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateMultiReader[M]
    with AggregateSingleWriter[M]
    with AggregateMultiWriter[M]
    with AggregateSingleSoftDeletable[M] {
  override type IdType        = UserAccountId
  override type AggregateType = UserAccount
}

object UserAccountRepository {

  type OnRedisWithTask[A]     = ReaderT[Task, RedisConnection, A]
  type OnMemcachedWithTask[A] = ReaderT[Task, MemcachedConnection, A]
  type OnMemoryWithTask[A]    = Task[A]
  type BySlickWithTask[A]     = Task[A]
  type BySkinnyWithTask[A]    = ReaderT[Task, DBSession, A]
  type ByFree[A]              = Free[UserRepositoryDSL, A]

  def bySlickWithTask(profile: JdbcProfile, db: JdbcProfile#Backend#Database): UserAccountRepositoryBySlickWithTask =
    new UserAccountRepositoryBySlickWithTask(profile, db)

  def bySkinnyWithTask: UserAccountRepositoryBySkinnyWithTask = new UserAccountRepositoryBySkinnyWithTask

  def onRedisWithTask(
      expireDuration: Duration
  )(implicit actorSystem: ActorSystem): UserAccountRepositoryOnRedisWithTask =
    new UserAccountRepositoryOnRedisWithTask(expireDuration)

  def onMemcachedWithTask(
      expireDuration: Duration
  )(implicit actorSystem: ActorSystem): UserAccountRepositoryOnMemcachedWithTask =
    new UserAccountRepositoryOnMemcachedWithTask(expireDuration)

  def onMemoryWithTask(minSize: Option[Int] = None,
                       maxSize: Option[Int] = None,
                       expireDuration: Option[Duration] = None,
                       concurrencyLevel: Option[Int] = None,
                       maxWeight: Option[Int] = None): UserAccountRepositoryOnMemoryWithTask =
    new UserAccountRepositoryOnMemoryWithTask(minSize, maxSize, expireDuration, concurrencyLevel, maxWeight)

}
