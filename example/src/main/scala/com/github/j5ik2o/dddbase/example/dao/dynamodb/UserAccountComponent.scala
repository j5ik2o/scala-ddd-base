package com.github.j5ik2o.dddbase.example.dao.dynamodb
import java.time.{ Instant, ZoneId, ZonedDateTime }

import com.github.j5ik2o.dddbase.dynamodb.DynamoDBDaoSupport
import com.github.j5ik2o.reactive.aws.dynamodb.implicits._
import com.github.j5ik2o.reactive.aws.dynamodb.monix.DynamoDbMonixClient
import monix.eval.Task
import software.amazon.awssdk.services.dynamodb.model._

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

  case class UserAccountDao(client: DynamoDbMonixClient)
      extends Dao[Task, String, UserAccountRecord]
      with DaoSoftDeletable[Task, String, UserAccountRecord] {
    val tableName       = "UserAccount"
    val DELETED: String = "deleted"

    override def put(record: UserAccountRecord): Task[Long] = {
      client
        .putItem(
          tableName,
          Map(
            "Id"        -> AttributeValue.builder().s(record.id).build(),
            "Status"    -> AttributeValue.builder().s(record.status).build(),
            "Email"     -> AttributeValue.builder().s(record.email).build(),
            "Password"  -> AttributeValue.builder().s(record.password).build(),
            "FirstName" -> AttributeValue.builder().s(record.firstName).build(),
            "LastName"  -> AttributeValue.builder().s(record.lastName).build(),
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

    override def putMulti(records: Seq[UserAccountRecord]): Task[Long] = {
      client
        .batchWriteItem(
          Map(
            tableName -> records.map { record =>
              WriteRequest
                .builder().putRequest(
                  PutRequest
                    .builder().itemAsScala(
                      Map(
                        "Id"        -> AttributeValue.builder().s(record.id).build(),
                        "Status"    -> AttributeValue.builder().s(record.status).build(),
                        "Email"     -> AttributeValue.builder().s(record.email).build(),
                        "Password"  -> AttributeValue.builder().s(record.password).build(),
                        "FirstName" -> AttributeValue.builder().s(record.firstName).build(),
                        "LastName"  -> AttributeValue.builder().s(record.lastName).build(),
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
          if (response.sdkHttpResponse().isSuccessful) {
            Task.pure(records.size - response.unprocessedItems.size)
          } else
            Task.raiseError(new Exception())
        }
    }

    override def get(id: String): Task[Option[UserAccountRecord]] = {
      client.getItem(tableName, Map("Id" -> AttributeValue.builder().s(id).build())).flatMap { response =>
        if (response.sdkHttpResponse().isSuccessful) {
          Task.pure {
            response.itemAsScala.map { item =>
              UserAccountRecord(
                id = item("Id").s,
                status = item("Status").s,
                email = item("Email").s,
                password = item("Password").s,
                firstName = item("FirstName").s,
                lastName = item("LastName").s,
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

    override def getMulti(ids: Seq[String]): Task[Seq[UserAccountRecord]] = {
      client
        .batchGetItem(
          Map(
            tableName -> KeysAndAttributes
              .builder()
              .keysAsScala(ids.map { id =>
                Map("Id" -> AttributeValue.builder().s(id).build())
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
                      UserAccountRecord(
                        id = item("Id").s,
                        status = item("Status").s,
                        email = item("Email").s,
                        password = item("Password").s,
                        firstName = item("FirstName").s,
                        lastName = item("LastName").s,
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

    override def delete(id: String): Task[Long] = {
      client.deleteItem(tableName, Map("Id" -> AttributeValue.builder().s(id).build())).flatMap { response =>
        if (response.sdkHttpResponse().isSuccessful)
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
              WriteRequest
                .builder().deleteRequest(
                  DeleteRequest
                    .builder().keyAsScala(
                      Map(
                        "Id" -> AttributeValue.builder().s(id).build()
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

    override def softDelete(id: String): Task[Long] = {
      client
        .updateItem(
          tableName,
          Map("Id"     -> AttributeValue.builder().s(id).build()),
          Map("Status" -> AttributeValueUpdate.builder().value(AttributeValue.builder().s(DELETED).build()).build())
        )
        .flatMap { response =>
          if (response.sdkHttpResponse().isSuccessful) {
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
