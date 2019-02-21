package com.github.j5ik2o.dddbase.example.dao.dynamodb

import java.time.{ Instant, ZoneId, ZonedDateTime }

import com.github.j5ik2o.dddbase.dynamodb.DynamoDBDaoSupport
import com.github.j5ik2o.reactive.dynamodb.model._
import com.github.j5ik2o.reactive.dynamodb.monix.DynamoDBTaskClientV2
import monix.eval.Task

trait UserMessageComponent extends DynamoDBDaoSupport {

  case class UserMessageRecordId(userId: Long, messageId: Long)

  case class UserMessageRecord(id: UserMessageRecordId,
                               status: String,
                               message: String,
                               createdAt: java.time.ZonedDateTime,
                               updatedAt: Option[java.time.ZonedDateTime])
      extends SoftDeletableRecord[UserMessageRecordId] {
    override type This = UserMessageRecord
    override def withStatus(value: String): UserMessageRecord =
      copy(status = value)
  }

  case class UserMessageDao(client: DynamoDBTaskClientV2)
      extends Dao[Task, UserMessageRecordId, UserMessageRecord]
      with DaoSoftDeletable[Task, UserMessageRecordId, UserMessageRecord] {
    val tableName       = "UserMessage"
    val DELETED: String = "deleted"

    override def put(record: UserMessageRecord): Task[Long] = {
      client
        .putItem(
          tableName,
          Map(
            "UserId"    -> AttributeValue().withNumber(Some(record.id.userId.toString)),
            "MessageId" -> AttributeValue().withNumber(Some(record.id.messageId.toString)),
            "Status"    -> AttributeValue().withString(Some(record.status)),
            "Message"   -> AttributeValue().withString(Some(record.message)),
            "CreatedAt" -> AttributeValue().withNumber(Some(record.createdAt.toInstant.toEpochMilli.toString))
          ) ++ record.updatedAt
            .map { s =>
              Map("UpdatedAt" -> AttributeValue().withNumber(Some(s.toInstant.toEpochMilli.toString)))
            }
            .getOrElse(Map.empty)
        )
        .flatMap { response =>
          if (response.isSuccessful)
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
              WriteRequest().withPutRequest(
                Some(
                  PutRequest().withItem(
                    Some(
                      Map(
                        "UserId"    -> AttributeValue().withNumber(Some(record.id.userId.toString)),
                        "MessageId" -> AttributeValue().withNumber(Some(record.id.messageId.toString)),
                        "Status"    -> AttributeValue().withString(Some(record.status)),
                        "Message"   -> AttributeValue().withString(Some(record.message)),
                        "CreatedAt" -> AttributeValue()
                          .withNumber(Some(record.createdAt.toInstant.toEpochMilli.toString))
                      ) ++ record.updatedAt
                        .map { s =>
                          Map("UpdatedAt" -> AttributeValue().withNumber(Some(s.toInstant.toEpochMilli.toString)))
                        }
                        .getOrElse(Map.empty)
                    )
                  )
                )
              )
            }
          )
        )
        .flatMap { response =>
          if (response.isSuccessful) {
            Task.pure(records.size - response.unprocessedItems.size)
          } else
            Task.raiseError(new Exception())
        }
    }

    override def get(id: UserMessageRecordId): Task[Option[UserMessageRecord]] = {
      client
        .getItem(tableName,
                 Map(
                   "UserId"    -> AttributeValue().withNumber(Some(id.userId.toString)),
                   "MessageId" -> AttributeValue().withNumber(Some(id.messageId.toString))
                 ))
        .flatMap { response =>
          if (response.isSuccessful) {
            Task.pure {
              response.item.map { item =>
                UserMessageRecord(
                  id = UserMessageRecordId(item("UserId").number.get.toLong, item("MessageId").number.get.toLong),
                  status = item("Status").string.get,
                  message = item("Password").string.get,
                  createdAt = item("CreatedAt").number.map { v =>
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(v.toLong), ZoneId.systemDefault())
                  }.get,
                  updatedAt = item
                    .get("UpdatedAt")
                    .flatMap(_.number.map { v =>
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
            tableName -> KeysAndAttributes()
              .withKeys(Some(ids.map { id =>
                Map(
                  "UserId"    -> AttributeValue().withNumber(Some(id.userId.toString)),
                  "MessageId" -> AttributeValue().withNumber(Some(id.messageId.toString))
                )
              }))
          )
        )
        .flatMap { response =>
          if (response.isSuccessful) {
            Task.pure {
              response.responses
                .map { records =>
                  records.values.toSeq.flatMap { records =>
                    records.map { item =>
                      UserMessageRecord(
                        id = UserMessageRecordId(item("UserId").number.get.toLong, item("MessageId").number.get.toLong),
                        status = item("Status").string.get,
                        message = item("Password").string.get,
                        createdAt = item("CreatedAt").number.map { v =>
                          ZonedDateTime.ofInstant(Instant.ofEpochMilli(v.toLong), ZoneId.systemDefault())
                        }.get,
                        updatedAt = item("UpdatedAt").number.map { v =>
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
        .deleteItem(tableName,
                    Map(
                      "UserId"    -> AttributeValue().withNumber(Some(id.userId.toString)),
                      "MessageId" -> AttributeValue().withNumber(Some(id.messageId.toString))
                    ))
        .flatMap { response =>
          if (response.isSuccessful)
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
              WriteRequest().withDeleteRequest(
                Some(
                  DeleteRequest().withKey(
                    Some(
                      Map(
                        "UserId"    -> AttributeValue().withNumber(Some(id.userId.toString)),
                        "MessageId" -> AttributeValue().withNumber(Some(id.messageId.toString))
                      )
                    )
                  )
                )
              )
            }
          )
        )
        .flatMap { response =>
          if (response.isSuccessful) {
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
            "UserId"    -> AttributeValue().withNumber(Some(id.userId.toString)),
            "MessageId" -> AttributeValue().withNumber(Some(id.messageId.toString))
          ),
          Map("Status" -> AttributeValueUpdate().withValue(Some(AttributeValue().withString(Some(DELETED)))))
        )
        .flatMap { response =>
          if (response.isSuccessful) {
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
