package com.github.j5ik2o.dddbase.example.repository

import java.net.InetSocketAddress
import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.routing.DefaultResizer
import akka.testkit.TestKit
import cats.free.Free
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.ByFree
import com.github.j5ik2o.dddbase.example.repository.free.{UserAccountRepositoryByFree, UserRepositoryDSL}
import com.github.j5ik2o.dddbase.example.repository.util.{
  FlywayWithMySQLSpecSupport,
  SkinnySpecSupport,
  Slick3SpecSupport
}
import com.github.j5ik2o.reactive.memcached.{
  MemcachedConnection,
  MemcachedConnectionPool,
  MemcachedSpecSupport,
  PeerConfig => MemcachedPeerConfig
}
import com.github.j5ik2o.reactive.redis.{PeerConfig => RedisPeerConfig}
import com.github.j5ik2o.reactive.redis._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{FreeSpecLike, Matchers}
import scalikejdbc.AutoSession
import scala.concurrent.duration._

class UserAccountRepositorySpec
    extends TestKit(ActorSystem("UserAccountRepositorySpec"))
    with FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with Slick3SpecSupport
    with SkinnySpecSupport
    with RedisSpecSupport
    with MemcachedSpecSupport
    with Matchers {

  override val tables: Seq[String] = Seq("user_account")

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

  "UserAccountRepository" - {
    "slick" - {
      "store" in {
        val slickRepo = UserAccountRepository.bySlickWithTask(dbConfig.profile, dbConfig.db)
        val result1   = slickRepo.store(userAccount).runAsync.futureValue
        assert(result1 == 1L)
        val result2 = slickRepo.store(userAccount).runAsync.futureValue
        assert(result2 == 1L)
        slickRepo.resolveById(userAccount.id).runAsync.futureValue
      }
      "storeMulti" in {
        val slickRepo = UserAccountRepository.bySlickWithTask(dbConfig.profile, dbConfig.db)
        val result1   = slickRepo.storeMulti(userAccounts).runAsync.futureValue
        assert(result1 == 10L)
      }
    }
    "skinny" - {
      "store" in {
        val skinnyRepo = UserAccountRepository.bySkinnyWithTask
        val result1    = skinnyRepo.store(userAccount).run(AutoSession).runAsync.futureValue
        assert(result1 == 1L)
        val result2 = skinnyRepo.store(userAccount).run(AutoSession).runAsync.futureValue
        assert(result2 == 1L)
      }
      "storeMulti" in {
        val skinnyRepo = UserAccountRepository.bySkinnyWithTask
        val result1    = skinnyRepo.storeMulti(userAccounts).run(AutoSession).runAsync.futureValue
        result1 shouldBe userAccounts.size
      }
    }
    "redis" - {
      "test" in {
        val repos      = UserAccountRepository.onRedisWithTask
        val peerConfig = RedisPeerConfig(new InetSocketAddress("127.0.0.1", redisMasterServer.getPort))
        val connectionPool =
          RedisConnectionPool.ofSingleRoundRobin(sizePerPeer = 3,
                                                 peerConfig,
                                                 RedisConnection(_, _),
                                                 reSizer = Some(DefaultResizer(lowerBound = 1, upperBound = 5)))
        connectionPool
          .withConnectionF { con =>
            (for {
              _ <- repos.store(userAccount)
              r <- repos.resolveById(userAccount.id)
            } yield r).run(con)
          }
          .runAsync
          .futureValue
      }
    }
    "memcached" - {
      "test" in {
        val repos      = UserAccountRepository.onMemcachedWithTask
        val peerConfig = MemcachedPeerConfig(new InetSocketAddress("127.0.0.1", memcachedTestServer.getPort))
        val connectionPool =
          MemcachedConnectionPool.ofSingleRoundRobin(sizePerPeer = 3,
                                                     peerConfig,
                                                     MemcachedConnection(_, _),
                                                     reSizer = Some(DefaultResizer(lowerBound = 1, upperBound = 5)))
        connectionPool
          .withConnectionF { con =>
            (for {
              _ <- repos.store(userAccount)
              r <- repos.resolveById(userAccount.id)
            } yield r).run(con)
          }
          .runAsync
          .futureValue
      }
    }
    "free" - {
      "skinny" in {
        val free = UserAccountRepository[ByFree]
        val program: Free[UserRepositoryDSL, UserAccount] = for {
          _      <- free.store(userAccount)
          result <- free.resolveById(userAccount.id)
        } yield result

        val skinny     = UserAccountRepository.bySkinnyWithTask
        val evalResult = UserAccountRepositoryByFree.evaluate(skinny)(program)
        val result     = evalResult.run(AutoSession).runAsync.futureValue
        result shouldBe userAccount
      }
      "slick" in {
        val free = UserAccountRepository[ByFree]
        val program = for {
          _      <- free.store(userAccount)
          result <- free.resolveById(userAccount.id)
        } yield result

        val slick                         = UserAccountRepository.bySlickWithTask(dbConfig.profile, dbConfig.db)
        val evalResult: Task[UserAccount] = UserAccountRepositoryByFree.evaluate(slick)(program)
        val result                        = evalResult.runAsync.futureValue
        result shouldBe userAccount
      }
    }
  }
}
