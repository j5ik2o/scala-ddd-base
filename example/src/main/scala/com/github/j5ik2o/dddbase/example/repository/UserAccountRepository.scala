package com.github.j5ik2o.dddbase.example.repository

import _root_.slick.jdbc.JdbcProfile
import cats.data.ReaderT
import cats.free.Free
import com.github.j5ik2o.dddbase._
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.free.{UserAccountRepositoryOnFree, UserRepositoryDSL}
import com.github.j5ik2o.dddbase.example.repository.skinny.UserAccountRepositoryOnSkinny
import com.github.j5ik2o.dddbase.example.repository.slick.UserAccountRepositoryOnSlick
import monix.eval.Task
import scalikejdbc.DBSession

trait UserAccountRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateMultiReader[M]
    with AggregateSingleWriter[M]
    with AggregateMultiWriter[M]
    with AggregateSoftDeletable[M] {
  override type IdType        = UserAccountId
  override type AggregateType = UserAccount
}

object UserAccountRepository {

  type BySlick[A]  = Task[A]
  type BySkinny[A] = ReaderT[Task, DBSession, A]
  type ByFree[A]   = Free[UserRepositoryDSL, A]

  def bySlick(profile: JdbcProfile, db: JdbcProfile#Backend#Database): UserAccountRepository[BySlick] =
    new UserAccountRepositoryOnSlick(profile, db)

  def bySkinny: UserAccountRepository[BySkinny] = new UserAccountRepositoryOnSkinny

  implicit val skinny: UserAccountRepository[BySkinny] = bySkinny

  implicit val free: UserAccountRepository[ByFree] = UserAccountRepositoryOnFree

  def apply[M[_]](implicit F: UserAccountRepository[M]): UserAccountRepository[M] = F

}
