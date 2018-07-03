package com.github.j5ik2o.dddbase.example.repository.slick

import _root_.slick.jdbc.JdbcProfile
import com.github.j5ik2o.dddbase.example.dao.slick.UserAccountComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.slick._
import monix.eval.Task

class UserAccountRepositoryBySlickWithTask(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
    extends UserAccountRepository[UserAccountRepository.BySlickWithTask]
    with AggregateSingleReadFeature
    with AggregateMultiReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiWriteFeature
    with AggregateSingleSoftDeleteFeature
    with UserAccountComponent {

  override type RecordType = UserAccountRecord
  override type TableType  = UserAccounts
  override protected val dao = UserAccountDao

  override protected def convertToAggregate(record: UserAccountRecord): RIO[UserAccount] =
    Task.pure {
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
    }

  override protected def convertToRecord(aggregate: UserAccount): RIO[UserAccountRecord] =
    Task.pure {
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
    }

}
