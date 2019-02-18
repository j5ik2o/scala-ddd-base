package com.github.j5ik2o.dddbase.example.repository.redis

import java.net.InetSocketAddress
import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.routing.DefaultResizer
import akka.testkit.TestKit
import com.github.j5ik2o.dddbase.AggregateNotFoundException
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ IdGenerator, UserAccountRepository }
import com.github.j5ik2o.dddbase.example.repository.util.ScalaFuturesSupportSpec
import com.github.j5ik2o.reactive.redis._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FreeSpecLike, Matchers }

import scala.concurrent.Await
import scala.concurrent.duration._

class UserAccountRepositoryOnRedisSpec
    extends TestKit(ActorSystem("UserAccountRepositoryOnRedisSpec"))
    with FreeSpecLike
    with RedisSpecSupport
    with ScalaFutures
    with ScalaFuturesSupportSpec
    with Matchers {

  var connectionPool: RedisConnectionPool[Task] = _

  override def waitFor(): Unit = {
    Thread.sleep(500 * sys.env.get("SBT_TEST_TIME_FACTOR").map(_.toLong).getOrElse(1L))
  }

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    val peerConfig = PeerConfig(new InetSocketAddress("127.0.0.1", redisMasterServer.getPort))
    connectionPool = RedisConnectionPool.ofSingleRoundRobin(sizePerPeer = 3,
                                                            peerConfig,
                                                            RedisConnection.apply,
                                                            reSizer =
                                                              Some(DefaultResizer(lowerBound = 1, upperBound = 5)))

  }

  protected override def afterAll(): Unit = {
    super.afterAll()
    waitFor()
    TestKit.shutdownActorSystem(system)
  }

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
        updatedAt = None
      )

  "UserAccountRepositoryOnRedis" - {
    "store" in {
      val repository = UserAccountRepository.onRedis(expireDuration = Duration.Inf)
      val result = connectionPool
        .withConnectionF { con =>
          (for {
            _ <- repository.store(userAccount)
            r <- repository.resolveById(userAccount.id)
          } yield r).run(con)
        }
        .runToFuture
        .futureValue

      result shouldBe userAccount
    }
    "storeMulti" in {
      val repository = UserAccountRepository.onRedis(expireDuration = Duration.Inf)
      val result = connectionPool
        .withConnectionF { con =>
          (for {
            _ <- repository.storeMulti(userAccounts)
            r <- repository.resolveMulti(userAccounts.map(_.id))
          } yield r).run(con)
        }
        .runToFuture
        .futureValue

      result shouldBe userAccounts
    }
    "store then expired" in {
      val repository = UserAccountRepository.onRedis(expireDuration = 1 seconds)
      val resultFuture = connectionPool.withConnectionF { con =>
        (for {
          _ <- repository.store(userAccount)
          _ <- ReaderTTask.pure(Thread.sleep(3000))
          r <- repository.resolveById(userAccount.id)
        } yield r).run(con)
      }.runToFuture

      an[AggregateNotFoundException] should be thrownBy {
        Await.result(resultFuture, Duration.Inf)
      }
    }
  }

}
