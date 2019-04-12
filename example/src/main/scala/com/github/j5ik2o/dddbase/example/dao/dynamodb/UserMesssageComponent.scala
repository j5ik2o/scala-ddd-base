package com.github.j5ik2o.dddbase.example.dao.dynamodb

import java.time.{ Instant, ZoneId, ZonedDateTime }

import com.github.j5ik2o.dddbase.dynamodb.DynamoDBDaoSupport
import com.github.j5ik2o.reactive.aws.dynamodb.implicits._
import com.github.j5ik2o.reactive.aws.dynamodb.monix.DynamoDbMonixClient
import monix.eval.Task
import software.amazon.awssdk.services.dynamodb.model._

trait UserMessageComponent extends DynamoDBDaoSupport {

  case class UserMessageRecordId(userId: Long, messageId: Long)

  case class UserMessageRecord(
      id: UserMessageRecordId,
      status: String,
      message: String,
      createdAt: java.time.ZonedDateTime,
      updatedAt: Option[java.time.ZonedDateTime]
  ) extends SoftDeletableRecord[UserMessageRecordId] {
    override type This = UserMessageRecord
    override def withStatus(value: String): UserMessageRecord =
      copy(status = value)
  }

  case class UserMessageDao(client: DynamoDbMonixClient)
      extends Dao[Task, UserMessageRecordId, UserMessageRecord]
      with DaoSoftDeletable[Task, UserMessageRecordId, UserMessageRecord] {
    val tableName       = "UserMessage"
    val DELETED: String = "deleted"

    override def put(record: UserMessageRecord): Task[Long] = {
      client
        .putItem(
          tableName,
          Map(
            "UserId"    -> AttributeValue.builder().n(record.id.userId.toString).build(),
            "MessageId" -> AttributeValue.builder().n(record.id.messageId.toString).build(),
            "Status"    -> AttributeValue.builder().s(record.status).build(),
            "Message"   -> AttributeValue.builder().s(record.message).build(),
            "CreatedAt" -> AttributeValue.builder().n(record.createdAt.toInstant.toEpochMilli.toString).build()
          ) ++ record.updatedAt
            .map { s =>
              Map("UpdatedAt" -> AttributeValue.builder().n(s.toInstant.toEpochMilli.toString).build())
            }
            .getOrElse(Map.empty)
        )
        .flatMap { response =>
          if (response.sdkHttpResponse().isSuccessful)
            Task.pure(1L)
          else
            Task.raiseError(new Exception())
        }
    }

    override def putMulti(records: Seq[UserMessageRecord]): Task[Long] = {
      client
        .batchWriteItem(
          Map(
            tableName -> records.map { record =>
              WriteRequest
                .builder().putRequest(
                  PutRequest
                    .builder().itemAsScala(
                      Map(
                        "UserId"    -> AttributeValue.builder().n(record.id.userId.toString).build(),
                        "MessageId" -> AttributeValue.builder().n(record.id.messageId.toString).build(),
                        "Status"    -> AttributeValue.builder().s(record.status).build(),
                        "Message"   -> AttributeValue.builder().s(record.message).build(),
                        "CreatedAt" -> AttributeValue
                          .builder()
                          .n(record.createdAt.toInstant.toEpochMilli.toString).build()
                      ) ++ record.updatedAt
                        .map { s =>
                          Map("UpdatedAt" -> AttributeValue.builder().n(s.toInstant.toEpochMilli.toString).build())
                        }
                        .getOrElse(Map.empty)
                    ).build()
                ).build()
            }
          )
        )
        .flatMap { response =>
          if (response.sdkHttpResponse.isSuccessful) {
            Task.pure(records.size - response.unprocessedItems.size)
          } else
            Task.raiseError(new Exception())
        }
    }

    override def get(id: UserMessageRecordId): Task[Option[UserMessageRecord]] = {
      client
        .getItem(
          tableName,
          Map(
            "UserId"    -> AttributeValue.builder().n(id.userId.toString).build(),
            "MessageId" -> AttributeValue.builder().n(id.messageId.toString).build()
          )
        )
        .flatMap { response =>
          if (response.sdkHttpResponse().isSuccessful) {
            Task.pure {
              response.itemAsScala.map { item =>
                UserMessageRecord(
                  id = UserMessageRecordId(item("UserId").n.toLong, item("MessageId").n.toLong),
                  status = item("Status").s,
                  message = item("Password").s,
                  createdAt = item("CreatedAt").nAsScala.map { v =>
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(v.toLong), ZoneId.systemDefault())
                  }.get,
                  updatedAt = item
                    .get("UpdatedAt")
                    .flatMap(_.nAsScala.map { v =>
                      ZonedDateTime.ofInstant(Instant.ofEpochMilli(v.toLong), ZoneId.systemDefault())
                    })
                )
              }
            }
          } else
            Task.raiseError(new Exception())
        }

    }
    override def getMulti(ids: Seq[UserMessageRecordId]): Task[Seq[UserMessageRecord]] = {
      client
        .batchGetItem(
          Map(
            tableName -> KeysAndAttributes
              .builder()
              .keysAsScala(ids.map { id =>
                Map(
                  "UserId"    -> AttributeValue.builder().n(id.userId.toString).build(),
                  "MessageId" -> AttributeValue.builder().n(id.messageId.toString).build()
                )
              }).build()
          )
        )
        .flatMap { response =>
          if (response.sdkHttpResponse().isSuccessful) {
            Task.pure {
              response.responsesAsScala
                .map { records =>
                  records.values.toSeq.flatMap { records =>
                    records.map { item =>
                      UserMessageRecord(
                        id = UserMessageRecordId(item("UserId").n.toLong, item("MessageId").n.toLong),
                        status = item("Status").s,
                        message = item("Password").s,
                        createdAt = item("CreatedAt").nAsScala.map { v =>
                          ZonedDateTime.ofInstant(Instant.ofEpochMilli(v.toLong), ZoneId.systemDefault())
                        }.get,
                        updatedAt = item("UpdatedAt").nAsScala.map { v =>
                          ZonedDateTime.ofInstant(Instant.ofEpochMilli(v.toLong), ZoneId.systemDefault())
                        }
                      )
                    }
                  }
                }
                .getOrElse(Seq.empty)
            }
          } else
            Task.raiseError(new Exception())
        }

    }

    override def delete(id: UserMessageRecordId): Task[Long] = {
      client
        .deleteItem(
          tableName,
          Map(
            "UserId"    -> AttributeValue.builder().n(id.userId.toString).build(),
            "MessageId" -> AttributeValue.builder().n(id.messageId.toString).build()
          )
        )
        .flatMap { response =>
          if (response.sdkHttpResponse().isSuccessful)
            Task.pure(1L)
          else
            Task.raiseError(new Exception())
        }
    }

    override def deleteMulti(ids: Seq[UserMessageRecordId]): Task[Long] = {
      client
        .batchWriteItem(
          Map(
            tableName -> ids.map { id =>
              WriteRequest
                .builder().deleteRequest(
                  DeleteRequest
                    .builder().keyAsScala(
                      Map(
                        "UserId"    -> AttributeValue.builder().n(id.userId.toString).build(),
                        "MessageId" -> AttributeValue.builder().n(id.messageId.toString).build()
                      )
                    ).build()
                ).build()
            }
          )
        )
        .flatMap { response =>
          if (response.sdkHttpResponse().isSuccessful) {
            Task.pure(ids.size - response.unprocessedItems.size)
          } else
            Task.raiseError(new Exception())
        }
    }

    override def softDelete(id: UserMessageRecordId): Task[Long] = {
      client
        .updateItem(
          tableName,
          Map(
            "UserId"    -> AttributeValue.builder().n(id.userId.toString).build(),
            "MessageId" -> AttributeValue.builder().n(id.messageId.toString).build()
          ),
          Map("Status" -> AttributeValueUpdate.builder().value(AttributeValue.builder().s(DELETED).build()).build())
        )
        .flatMap { response =>
          if (response.sdkHttpResponse().isSuccessful) {
            Task.pure(1L)
          } else
            Task.raiseError(new Exception)
        }
    }

    override def softDeleteMulti(ids: Seq[UserMessageRecordId]): Task[Long] = {
      Task
        .traverse(ids) { id =>
          delete(id)
        }
        .map(_.count(_ > 0))
    }
  }

}
