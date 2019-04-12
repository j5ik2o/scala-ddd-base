package com.github.j5ik2o.dddbase.example.repository.dynamodb

import java.net.URI
import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ IdGenerator, SpecSupport }
import com.github.j5ik2o.reactive.aws.dynamodb.implicits._
import com.github.j5ik2o.reactive.aws.dynamodb.monix.DynamoDbMonixClient
import com.github.j5ik2o.reactive.aws.dynamodb.{ DynamoDBEmbeddedSpecSupport, DynamoDbAsyncClient }
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FreeSpec, Matchers }
import software.amazon.awssdk.auth.credentials.{ AwsBasicCredentials, StaticCredentialsProvider }
import software.amazon.awssdk.services.dynamodb.model._
import software.amazon.awssdk.services.dynamodb.{ DynamoDbAsyncClient => JavaDynamoDbAsyncClient }

import scala.concurrent.duration._

class UserAccountRepositoryOnDynamoDBSpec
    extends FreeSpec
    with Matchers
    with ScalaFutures
    with DynamoDBEmbeddedSpecSupport
    with SpecSupport {
  implicit val pc: PatienceConfig = PatienceConfig(20 seconds, 1 seconds)

  val underlying: JavaDynamoDbAsyncClient = JavaDynamoDbAsyncClient
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

  val client: DynamoDbMonixClient = DynamoDbMonixClient(DynamoDbAsyncClient(underlying))

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
    val createRequest = CreateTableRequest
      .builder()
      .attributeDefinitionsAsScala(
        Seq(
          AttributeDefinition
            .builder()
            .attributeName("Id")
            .attributeType(ScalarAttributeType.S).build()
        )
      )
      .keySchemaAsScala(
        Seq(
          KeySchemaElement
            .builder()
            .attributeName("Id")
            .keyType(KeyType.HASH).build()
        )
      )
      .provisionedThroughput(
        ProvisionedThroughput
          .builder()
          .readCapacityUnits(10L)
          .writeCapacityUnits(10L).build()
      )
      .tableName(tableName).build()
    val createResponse = client
      .createTable(createRequest)
      .runToFuture
      .futureValue
    (tableName, createResponse)
  }
}
