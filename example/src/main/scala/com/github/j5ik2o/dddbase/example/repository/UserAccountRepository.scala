package com.github.j5ik2o.dddbase.example.repository

import _root_.slick.jdbc.JdbcProfile
import akka.actor.ActorSystem
import cats.data.ReaderT
import cats.free.Free
import com.github.j5ik2o.dddbase._
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.free.{UserAccountRepositoryByFree, UserRepositoryDSL}
import com.github.j5ik2o.dddbase.example.repository.redis.UserAccountRepositoryOnRedisWithTask
import com.github.j5ik2o.dddbase.example.repository.skinny.UserAccountRepositoryBySkinnyWithTask
import com.github.j5ik2o.dddbase.example.repository.slick.UserAccountRepositoryBySlickWithTask
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task
import scalikejdbc.DBSession

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

  type OnRedisWithTask[A]  = ReaderT[Task, RedisConnection, A]
  type BySlickWithTask[A]  = Task[A]
  type BySkinnyWithTask[A] = ReaderT[Task, DBSession, A]
  type ByFree[A]           = Free[UserRepositoryDSL, A]

  def bySlickWithTask(profile: JdbcProfile, db: JdbcProfile#Backend#Database): UserAccountRepository[BySlickWithTask] =
    new UserAccountRepositoryBySlickWithTask(profile, db)

  def bySkinnyWithTask: UserAccountRepository[BySkinnyWithTask] = new UserAccountRepositoryBySkinnyWithTask

  def onRedisWithTask(implicit actorSystem: ActorSystem): UserAccountRepository[OnRedisWithTask] =
    new UserAccountRepositoryOnRedisWithTask()

  implicit val skinny: UserAccountRepository[BySkinnyWithTask] = bySkinnyWithTask

  implicit val free: UserAccountRepository[ByFree] = UserAccountRepositoryByFree

  def apply[M[_]](implicit F: UserAccountRepository[M]): UserAccountRepository[M] = F

}
