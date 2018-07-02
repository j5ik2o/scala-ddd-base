package com.github.j5ik2o.dddbase.example.repository

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase._
import monix.eval.Task
import _root_.slick.jdbc.JdbcProfile
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO

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

  def ofSlick(profile: JdbcProfile, db: JdbcProfile#Backend#Database): UserAccountRepository[Task] =
    new UserAccountRepositoryOnSlick(profile, db)

//  private class Skinny
//      extends UserAccountRepository[AggregateIOBaseFeature.RIO]
//      with skinny.AggregateSingleReadFeature {
//    override type RecordType = this.type
//    override type DaoType = this.type
//    override protected val dao: Skinny.this.type = _
//
//
//    override protected def convertToAggregate(record: Skinny.this.type): RIO[UserAccount] = ???
//
//  }

  private class UserAccountRepositoryOnSlick(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
      extends UserAccountRepository[Task]
      with slick.AggregateSingleReadFeature
      with slick.AggregateMultiReadFeature
      with slick.AggregateSingleWriteFeature
      with slick.AggregateMultiWriteFeature
      with slick.AggregateSoftDeleteFeature
      with com.github.j5ik2o.dddbase.example.dao.slick.UserAccountComponent {

    override type RecordType = UserAccountRecord
    override type TableType  = UserAccounts
    override protected val dao = UserAccountDao

    override protected def convertToAggregate(record: UserAccountRecord): Task[UserAccount] = Task.now(
      UserAccount(
        id = UserAccountId(record.id),
        status = Status.withName(record.status),
        emailAddress = EmailAddress(record.email),
        password = HashedPassword(record.password),
        firstName = record.firstName,
        lastName = record.lastName,
        createdAt = record.createdAt,
        updatedAt = record.updatedAt
      )
    )

    override protected def convertToRecord(aggregate: UserAccount): Task[UserAccountRecord] = Task.now(
      UserAccountRecord(
        id = aggregate.id.value,
        status = aggregate.status.entryName,
        email = aggregate.emailAddress.value,
        password = aggregate.password.value,
        firstName = aggregate.firstName,
        lastName = aggregate.lastName,
        createdAt = aggregate.createdAt,
        updatedAt = aggregate.updatedAt
      )
    )

  }

}
