package com.github.j5ik2o.dddbase.example.repository.memcached

import java.net.InetSocketAddress
import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.routing.DefaultResizer
import akka.testkit.TestKit
import com.github.j5ik2o.dddbase.AggregateNotFoundException
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.util.ScalaFuturesSupportSpec
import com.github.j5ik2o.reactive.memcached._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class UserAccountRepositoryOnMemcachedSpec
    extends TestKit(ActorSystem("UserAccountRepositoryOnMemcachedSpec"))
    with FreeSpecLike
    with MemcachedSpecSupport
    with ScalaFutures
    with ScalaFuturesSupportSpec
    with Matchers {

  var connectionPool: MemcachedConnectionPool[Task] = _

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    val peerConfig = PeerConfig(new InetSocketAddress("127.0.0.1", memcachedTestServer.getPort))
    connectionPool = MemcachedConnectionPool.ofSingleRoundRobin(sizePerPeer = 3,
                                                                peerConfig,
                                                                MemcachedConnection(_, _),
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

  "UserAccountRepositoryOnMemcached" - {
    "store" in {
      val repository = UserAccountRepository.onMemcached(expireDuration = Duration.Inf)
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
      val repository = UserAccountRepository.onMemcached(expireDuration = Duration.Inf)
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
    "store then expired" in {
      val repository = UserAccountRepository.onMemcached(expireDuration = 1 seconds)
      val resultFuture = connectionPool.withConnectionF { con =>
        (for {
          _ <- repository.store(userAccount)
          _ <- ReaderTTask.pure(Thread.sleep(1000))
          r <- repository.resolveById(userAccount.id)
        } yield r).run(con)
      }.runAsync

      an[AggregateNotFoundException] should be thrownBy {
        Await.result(resultFuture, Duration.Inf)
      }
    }
  }

}
