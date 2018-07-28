package com.github.j5ik2o.dddbase.example.repository.redis

import java.net.InetSocketAddress
import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.routing.DefaultResizer
import akka.testkit.TestKit
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.util.ScalaFuturesSupportSpec
import com.github.j5ik2o.reactive.redis.{ PeerConfig, RedisConnection, RedisConnectionPool, RedisSpecSupport }
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FreeSpecLike, Matchers }

class UserAccountRepositoryOnRedisWithTaskSpec
    extends TestKit(ActorSystem("UserAccountRepositoryOnRedisWithTaskSpec"))
    with FreeSpecLike
    with RedisSpecSupport
    with ScalaFutures
    with ScalaFuturesSupportSpec
    with Matchers {

  val repository = UserAccountRepository.onRedisWithTask

  var connectionPool: RedisConnectionPool[Task] = _

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    val peerConfig = PeerConfig(new InetSocketAddress("127.0.0.1", redisMasterServer.getPort))
    connectionPool = RedisConnectionPool.ofSingleRoundRobin(sizePerPeer = 3,
                                                            peerConfig,
                                                            RedisConnection(_, _),
                                                            reSizer =
                                                              Some(DefaultResizer(lowerBound = 1, upperBound = 5)))

  }

  val userAccount = UserAccount(
    id = UserAccountId(1L),
    status = Status.Active,
    emailAddress = EmailAddress("test@test.com"),
    password = HashedPassword("aaa"),
    firstName = "Junichi",
    lastName = "Kato",
    createdAt = ZonedDateTime.now,
    updatedAt = None
  )

  val userAccounts = for (idValue <- 1L to 10L)
    yield
      UserAccount(
        id = UserAccountId(idValue),
        status = Status.Active,
        emailAddress = EmailAddress(s"user${idValue}@gmail.com"),
        password = HashedPassword("aaa"),
        firstName = "Junichi",
        lastName = "Kato",
        createdAt = ZonedDateTime.now,
        updatedAt = None
      )

  "UserAccountRepositoryOnRedisWithTask" - {
    "store" in {
      val result = connectionPool
        .withConnectionF { con =>
          (for {
            _ <- repository.store(userAccount)
            r <- repository.resolveById(userAccount.id)
          } yield r).run(con)
        }
        .runAsync
        .futureValue

      result shouldBe userAccount
    }
    "storeMulti" in {
      val result = connectionPool
        .withConnectionF { con =>
          (for {
            _ <- repository.storeMulti(userAccounts)
            r <- repository.resolveMulti(userAccounts.map(_.id))
          } yield r).run(con)
        }
        .runAsync
        .futureValue

      result shouldBe userAccounts
    }
  }

}
