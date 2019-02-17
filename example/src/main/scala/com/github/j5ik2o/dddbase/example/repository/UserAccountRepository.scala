package com.github.j5ik2o.dddbase.example.repository

import _root_.slick.jdbc.JdbcProfile
import akka.actor.ActorSystem
import com.github.j5ik2o.dddbase._
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.dynamodb.UserAccountRepositoryOnDynamoDB
import com.github.j5ik2o.dddbase.example.repository.memcached.UserAccountRepositoryOnMemcached
import com.github.j5ik2o.dddbase.example.repository.memory.UserAccountRepositoryOnMemory
import com.github.j5ik2o.dddbase.example.repository.redis.UserAccountRepositoryOnRedis
import com.github.j5ik2o.dddbase.example.repository.skinny.{
  UserAccountRepositoryBySkinny,
  UserAccountRepositoryBySkinnyImpl
}
import com.github.j5ik2o.dddbase.example.repository.slick.UserAccountRepositoryBySlickImpl
import com.github.j5ik2o.reactive.dynamodb.monix.DynamoDBTaskClientV2
import com.google.common.base.Ticker

import scala.concurrent.duration.Duration

trait UserAccountRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateSingleWriter[M]
    with AggregateMultiReader[M]
    with AggregateMultiWriter[M]
    with AggregateSingleSoftDeletable[M]
    with AggregateMultiSoftDeletable[M] {
  override type IdType        = UserAccountId
  override type AggregateType = UserAccount

}

object UserAccountRepository {

  def bySlick(profile: JdbcProfile, db: JdbcProfile#Backend#Database): UserAccountRepository[BySlick] =
    new UserAccountRepositoryBySlickImpl(profile, db)

  def bySkinny: UserAccountRepository[BySkinny] = new UserAccountRepositoryBySkinnyImpl

  def onDynamoDB(client: DynamoDBTaskClientV2): UserAccountRepository[OnDynamoDB] =
    new UserAccountRepositoryOnDynamoDB(client)

  def onRedis(
      expireDuration: Duration
  )(implicit actorSystem: ActorSystem): UserAccountRepository[OnRedis] =
    new UserAccountRepositoryOnRedis(expireDuration)

  def onMemcached(
      expireDuration: Duration
  )(implicit actorSystem: ActorSystem): UserAccountRepository[OnMemcached] =
    new UserAccountRepositoryOnMemcached(expireDuration)

  def onMemory(concurrencyLevel: Option[Int] = None,
               expireAfterAccess: Option[Duration] = None,
               expireAfterWrite: Option[Duration] = None,
               initialCapacity: Option[Int] = None,
               maximumSize: Option[Int] = None,
               maximumWeight: Option[Int] = None,
               recordStats: Option[Boolean] = None,
               refreshAfterWrite: Option[Duration] = None,
               softValues: Option[Boolean] = None,
               ticker: Option[Ticker] = None,
               weakKeys: Option[Boolean] = None,
               weakValues: Option[Boolean] = None): UserAccountRepository[OnMemory] =
    new UserAccountRepositoryOnMemory(
      concurrencyLevel,
      expireAfterAccess,
      expireAfterWrite,
      initialCapacity,
      maximumSize,
      maximumWeight,
      recordStats,
      refreshAfterWrite,
      softValues,
      ticker,
      weakKeys,
      weakValues
    )

}
