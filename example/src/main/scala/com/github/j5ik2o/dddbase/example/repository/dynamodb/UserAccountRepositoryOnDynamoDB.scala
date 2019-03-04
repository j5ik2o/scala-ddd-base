package com.github.j5ik2o.dddbase.example.repository.dynamodb

import com.github.j5ik2o.dddbase.dynamodb._
import com.github.j5ik2o.dddbase.example.dao.dynamodb.UserAccountComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ OnDynamoDB, UserAccountRepository }
import com.github.j5ik2o.reactive.aws.dynamodb.monix.DynamoDBTaskClientV2
import monix.eval.Task

class UserAccountRepositoryOnDynamoDB(client: DynamoDBTaskClientV2)
    extends UserAccountRepository[OnDynamoDB]
    with AggregateSingleReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiReadFeature
    with AggregateMultiWriteFeature
    with AggregateSingleSoftDeleteFeature
    with AggregateMultiSoftDeleteFeature
    with UserAccountComponent {
  override type RecordIdType = String
  override type RecordType   = UserAccountRecord
  override type DaoType      = UserAccountDao
  override protected val dao = UserAccountDao(client)

  override protected def toRecordId(id: UserAccountId): String = id.value.toString

  override protected def convertToAggregate: UserAccountRecord => Task[UserAccount] = { record =>
    Task.pure {
      UserAccount(
        id = UserAccountId(record.id.toLong),
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

  override protected def convertToRecord: UserAccount => Task[UserAccountRecord] = { aggregate =>
    Task.pure {
      UserAccountRecord(
        id = aggregate.id.value.toString,
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
