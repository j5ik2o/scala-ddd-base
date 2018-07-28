package com.github.j5ik2o.dddbase.example.repository

import _root_.slick.jdbc.JdbcProfile
import akka.actor.ActorSystem
import cats.data.ReaderT
import cats.free.Free
import com.github.j5ik2o.dddbase._
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.free.{ UserAccountRepositoryByFree, UserRepositoryDSL }
import com.github.j5ik2o.dddbase.example.repository.memcached.UserAccountRepositoryOnMemcached
import com.github.j5ik2o.dddbase.example.repository.memory.UserAccountRepositoryOnMemory
import com.github.j5ik2o.dddbase.example.repository.redis.UserAccountRepositoryOnRedis
import com.github.j5ik2o.dddbase.example.repository.skinny.UserAccountRepositoryBySkinny
import com.github.j5ik2o.dddbase.example.repository.slick.UserAccountRepositoryBySlick
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

  type OnRedis[A]     = ReaderT[Task, RedisConnection, A]
  type OnMemcached[A] = ReaderT[Task, MemcachedConnection, A]
  type OnMemory[A]    = Task[A]
  type BySlick[A]     = Task[A]
  type BySkinny[A]    = ReaderT[Task, DBSession, A]
  type ByFree[A]      = Free[UserRepositoryDSL, A]

  def bySlick(profile: JdbcProfile, db: JdbcProfile#Backend#Database): UserAccountRepository[BySlick] =
    new UserAccountRepositoryBySlick(profile, db)

  def bySkinny: UserAccountRepository[BySkinny] = new UserAccountRepositoryBySkinny

  def onRedis(
      expireDuration: Duration
  )(implicit actorSystem: ActorSystem): UserAccountRepository[OnRedis] =
    new UserAccountRepositoryOnRedis(expireDuration)

  def onMemcached(
      expireDuration: Duration
  )(implicit actorSystem: ActorSystem): UserAccountRepository[OnMemcached] =
    new UserAccountRepositoryOnMemcached(expireDuration)

  def onMemory(minSize: Option[Int] = None,
               maxSize: Option[Int] = None,
               expireDuration: Option[Duration] = None,
               concurrencyLevel: Option[Int] = None,
               maxWeight: Option[Int] = None): UserAccountRepository[OnMemory] =
    new UserAccountRepositoryOnMemory(minSize, maxSize, expireDuration, concurrencyLevel, maxWeight)

}
