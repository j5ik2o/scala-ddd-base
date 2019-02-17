package com.github.j5ik2o.dddbase.example.repository

import com.github.j5ik2o.dddbase._
import com.github.j5ik2o.dddbase.example.model.{ UserMessage, UserMessageId }
import com.github.j5ik2o.dddbase.example.repository.dynamodb.UserMessageRepositoryOnDynamoDB
import com.github.j5ik2o.reactive.dynamodb.monix.DynamoDBTaskClientV2

trait UserMessageRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateSingleWriter[M]
    with AggregateMultiReader[M]
    with AggregateMultiWriter[M]
    with AggregateSingleSoftDeletable[M]
    with AggregateMultiSoftDeletable[M] {
  override type IdType        = UserMessageId
  override type AggregateType = UserMessage

}

object UserMessageRepository {

  def onDynamoDB(client: DynamoDBTaskClientV2): UserMessageRepository[OnDynamoDB] =
    new UserMessageRepositoryOnDynamoDB(client)

}
