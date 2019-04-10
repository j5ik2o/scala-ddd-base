package com.github.j5ik2o.dddbase.example.dao.dynamodb
import java.time.{ Instant, ZoneId, ZonedDateTime }

import com.github.j5ik2o.dddbase.dynamodb.DynamoDBDaoSupport
import com.github.j5ik2o.reactive.aws.dynamodb.model._
import com.github.j5ik2o.reactive.aws.dynamodb.monix.DynamoDBTaskClientV2
import monix.eval.Task

trait UserAccountComponent extends DynamoDBDaoSupport {

  case class UserAccountRecord(
      id: String,
      status: String,
      email: String,
      password: String,
      firstName: String,
      lastName: String,
      createdAt: java.time.ZonedDateTime,
      updatedAt: Option[java.time.ZonedDateTime]
  ) extends SoftDeletableRecord[String] {
    override type This = UserAccountRecord
    override def withStatus(value: String): UserAccountRecord =
      copy(status = value)
  }

  case class UserAccountDao(client: DynamoDBTaskClientV2)
      extends Dao[Task, String, UserAccountRecord]
      with DaoSoftDeletable[Task, String, UserAccountRecord] {
    val tableName       = "UserAccount"
    val DELETED: String = "deleted"

    override def put(record: UserAccountRecord): Task[Long] = {
      client
        .putItem(
          tableName,
          Map(
            "Id"        -> AttributeValue().withString(Some(record.id)),
            "Status"    -> AttributeValue().withString(Some(record.status)),
            "Email"     -> AttributeValue().withString(Some(record.email)),
            "Password"  -> AttributeValue().withString(Some(record.password)),
            "FirstName" -> AttributeValue().withString(Some(record.firstName)),
            "LastName"  -> AttributeValue().withString(Some(record.lastName)),
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

    override def putMulti(records: Seq[UserAccountRecord]): Task[Long] = {
      client
        .batchWriteItem(
          Map(
            tableName -> records.map { record =>
              WriteRequest().withPutRequest(
                Some(
                  PutRequest().withItem(
                    Some(
                      Map(
                        "Id"        -> AttributeValue().withString(Some(record.id)),
                        "Status"    -> AttributeValue().withString(Some(record.status)),
                        "Email"     -> AttributeValue().withString(Some(record.email)),
                        "Password"  -> AttributeValue().withString(Some(record.password)),
                        "FirstName" -> AttributeValue().withString(Some(record.firstName)),
                        "LastName"  -> AttributeValue().withString(Some(record.lastName)),
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

    override def get(id: String): Task[Option[UserAccountRecord]] = {
      client.getItem(tableName, Map("Id" -> AttributeValue().withString(Some(id)))).flatMap { response =>
        if (response.isSuccessful) {
          Task.pure {
            response.item.map { item =>
              UserAccountRecord(
                id = item("Id").string.get,
                status = item("Status").string.get,
                email = item("Email").string.get,
                password = item("Password").string.get,
                firstName = item("FirstName").string.get,
                lastName = item("LastName").string.get,
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

    override def getMulti(ids: Seq[String]): Task[Seq[UserAccountRecord]] = {
      client
        .batchGetItem(
          Map(
            tableName -> KeysAndAttributes()
              .withKeys(Some(ids.map { id =>
                Map("Id" -> AttributeValue().withString(Some(id)))
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
                      UserAccountRecord(
                        id = item("Id").string.get,
                        status = item("Status").string.get,
                        email = item("Email").string.get,
                        password = item("Password").string.get,
                        firstName = item("FirstName").string.get,
                        lastName = item("LastName").string.get,
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

    override def delete(id: String): Task[Long] = {
      client.deleteItem(tableName, Map("Id" -> AttributeValue().withString(Some(id)))).flatMap { response =>
        if (response.isSuccessful)
          Task.pure(1L)
        else
          Task.raiseError(new Exception())
      }
    }

    override def deleteMulti(ids: Seq[String]): Task[Long] = {
      client
        .batchWriteItem(
          Map(
            tableName -> ids.map { id =>
              WriteRequest().withDeleteRequest(
                Some(
                  DeleteRequest().withKey(
                    Some(
                      Map(
                        "Id" -> AttributeValue().withString(Some(id))
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

    override def softDelete(id: String): Task[Long] = {
      client
        .updateItem(
          tableName,
          Map("Id"     -> AttributeValue().withString(Some(id))),
          Map("Status" -> AttributeValueUpdate().withValue(Some(AttributeValue().withString(Some(DELETED)))))
        )
        .flatMap { response =>
          if (response.isSuccessful) {
            Task.pure(1L)
          } else
            Task.raiseError(new Exception)
        }
    }

    override def softDeleteMulti(ids: Seq[String]): Task[Long] = {
      Task
        .traverse(ids) { id =>
          delete(id)
        }
        .map(_.count(_ > 0))
    }
  }

}
