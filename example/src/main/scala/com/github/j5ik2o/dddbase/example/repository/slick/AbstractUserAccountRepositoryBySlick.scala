package com.github.j5ik2o.dddbase.example.repository.slick

import com.github.j5ik2o.dddbase.example.dao.slick.UserAccountComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ BySlick, UserAccountRepository }
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.slick.{
  AggregateMultiReadFeature,
  AggregateMultiWriteFeature,
  AggregateSingleReadFeature,
  AggregateSingleWriteFeature
}
import monix.eval.Task
import slick.jdbc.JdbcProfile
import slick.lifted.Rep

abstract class AbstractUserAccountRepositoryBySlick(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
    extends UserAccountRepository[BySlick]
    with AggregateSingleReadFeature
    with AggregateMultiReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiWriteFeature
    with UserAccountComponent {
  override type RecordType = UserAccountRecord
  override type TableType  = UserAccounts
  override protected val dao = UserAccountDao

  override protected def byCondition(id: IdType): TableType => Rep[Boolean] = {
    import profile.api._
    _.id === id.value
  }

  override protected def byConditions(ids: Seq[IdType]): TableType => Rep[Boolean] = {
    import profile.api._
    _.id.inSet(ids.map(_.value))
  }

  override protected def convertToAggregate: UserAccountRecord => RIO[UserAccount] = { record =>
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
  }

  override protected def convertToRecord: UserAccount => RIO[UserAccountRecord] = { aggregate =>
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

}
