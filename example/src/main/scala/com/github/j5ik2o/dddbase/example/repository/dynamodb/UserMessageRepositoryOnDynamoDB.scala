package com.github.j5ik2o.dddbase.example.repository.dynamodb

import com.github.j5ik2o.dddbase.dynamodb._
import com.github.j5ik2o.dddbase.example.dao.dynamodb.UserMessageComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ OnDynamoDB, UserMessageRepository }
import com.github.j5ik2o.reactive.aws.dynamodb.monix.DynamoDbMonixClient
import monix.eval.Task

class UserMessageRepositoryOnDynamoDB(client: DynamoDbMonixClient)
    extends UserMessageRepository[OnDynamoDB]
    with AggregateSingleReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiReadFeature
    with AggregateMultiWriteFeature
    with AggregateSingleSoftDeleteFeature
    with AggregateMultiSoftDeleteFeature
    with UserMessageComponent {
  override type RecordIdType = UserMessageRecordId
  override type RecordType   = UserMessageRecord
  override type DaoType      = UserMessageDao
  override protected val dao = UserMessageDao(client)

  override protected def toRecordId(
      id: UserMessageId
  ): UserMessageRecordId = UserMessageRecordId(id.userId, id.messageId)

  override protected def convertToAggregate: UserMessageRecord => Task[UserMessage] = { record =>
    Task.pure {
      UserMessage(
        id = UserMessageId(record.id.userId, record.id.messageId),
        status = Status.withName(record.status),
        message = record.message,
        createdAt = record.createdAt,
        updatedAt = record.updatedAt
      )
    }
  }

  override protected def convertToRecord: UserMessage => Task[UserMessageRecord] = { aggregate =>
    Task.pure {
      UserMessageRecord(
        id = UserMessageRecordId(aggregate.id.userId, aggregate.id.messageId),
        status = aggregate.status.entryName,
        message = aggregate.message,
        createdAt = aggregate.createdAt,
        updatedAt = aggregate.updatedAt
      )
    }
  }

}
