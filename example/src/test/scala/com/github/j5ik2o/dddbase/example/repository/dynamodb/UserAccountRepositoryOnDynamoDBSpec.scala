package com.github.j5ik2o.dddbase.example.repository.dynamodb

import java.net.URI
import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ IdGenerator, SpecSupport }
import com.github.j5ik2o.reactive.aws.dynamodb.model._
import com.github.j5ik2o.reactive.aws.dynamodb.monix.DynamoDBTaskClientV2
import com.github.j5ik2o.reactive.aws.dynamodb.DynamoDBAsyncClientV2
import com.github.j5ik2o.reactive.aws.dynamodb.DynamoDBEmbeddedSpecSupport
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FreeSpec, Matchers }
import software.amazon.awssdk.auth.credentials.{ AwsBasicCredentials, StaticCredentialsProvider }
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

import scala.concurrent.duration._

class UserAccountRepositoryOnDynamoDBSpec
    extends FreeSpec
    with Matchers
    with ScalaFutures
    with DynamoDBEmbeddedSpecSupport
    with SpecSupport {
  implicit val pc: PatienceConfig = PatienceConfig(20 seconds, 1 seconds)

  val underlying: DynamoDbAsyncClient = DynamoDbAsyncClient
    .builder()
    .credentialsProvider(
      StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))
    )
    .endpointOverride(URI.create(dynamoDBEndpoint))
    .build()

  val userAccount = UserAccount(
    id = UserAccountId(IdGenerator.generateIdValue),
    status = Status.Active,
    emailAddress = EmailAddress("test@test.com"),
    password = HashedPassword("aaa"),
    firstName = "Junichi",
    lastName = "Kato",
    createdAt = ZonedDateTime.now,
    updatedAt = None
  )

  val userAccounts = for (idx <- 1L to 10L)
    yield
      UserAccount(
        id = UserAccountId(IdGenerator.generateIdValue),
        status = Status.Active,
        emailAddress = EmailAddress(s"user${idx}@gmail.com"),
        password = HashedPassword("aaa"),
        firstName = "Junichi",
        lastName = "Kato",
        createdAt = ZonedDateTime.now,
        updatedAt = Some(ZonedDateTime.now)
      )

  val client: DynamoDBTaskClientV2 = DynamoDBTaskClientV2(DynamoDBAsyncClientV2(underlying))

  "UserAccountRepositoryOnDynamoDB" - {
    "store" in {
      createTable("UserAccount")
      val repository = new UserAccountRepositoryOnDynamoDB(client)
      val result =
        (for {
          _ <- repository.store(userAccount)
          r <- repository.resolveById(userAccount.id)
        } yield r).runToFuture.futureValue

      result shouldBe userAccount
    }
    "storeMulti" in {
      val repository = new UserAccountRepositoryOnDynamoDB(client)
      val result =
        (for {
          _ <- repository.storeMulti(userAccounts)
          r <- repository.resolveMulti(userAccounts.map(_.id))
        } yield r).runToFuture.futureValue

      sameAs(result, userAccounts) shouldBe true
    }
  }

  private def createTable(
      tableName: String
  ): (String, CreateTableResponse) = {
    val createRequest = CreateTableRequest()
      .withAttributeDefinitions(
        Some(
          Seq(
            AttributeDefinition()
              .withAttributeName(Some("Id"))
              .withAttributeType(Some(AttributeType.S))
          )
        )
      )
      .withKeySchema(
        Some(
          Seq(
            KeySchemaElement()
              .withAttributeName(Some("Id"))
              .withKeyType(Some(KeyType.HASH))
          )
        )
      )
      .withProvisionedThroughput(
        Some(
          ProvisionedThroughput()
            .withReadCapacityUnits(Some(10L))
            .withWriteCapacityUnits(Some(10L))
        )
      )
      .withTableName(Some(tableName))
    val createResponse = client
      .createTable(createRequest)
      .runToFuture
      .futureValue
    (tableName, createResponse)
  }
}
