package com.github.j5ik2o.dddbase.example.repository.memory

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.AggregateNotFoundException
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.util.ScalaFuturesSupportSpec
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class UserAccountRepositoryOnMemorySpec extends FreeSpec with ScalaFutures with ScalaFuturesSupportSpec with Matchers {

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

  "UserAccountRepositoryOnMemory" - {
    "store" in {
      val repository = UserAccountRepository.onMemory()
      val result: UserAccount = (for {
        _ <- repository.store(userAccount)
        r <- repository.resolveById(userAccount.id)
      } yield r).runAsync.futureValue

      result shouldBe userAccount
    }
    "storeMulti" in {
      val repository = UserAccountRepository.onMemory()
      val result: Seq[UserAccount] = (for {
        _ <- repository.storeMulti(userAccounts)

        r <- repository.resolveMulti(userAccounts.map(_.id))
      } yield r).runAsync.futureValue

      result shouldBe userAccounts
    }
    "store then expired" in {
      val repository = UserAccountRepository.onMemory(expireAfterWrite = Some(1 seconds))
      val resultFuture: Future[UserAccount] = (for {
        _ <- repository.store(userAccount)
        _ <- Task.pure(Thread.sleep(1000))
        r <- repository.resolveById(userAccount.id)
      } yield r).runAsync

      an[AggregateNotFoundException] should be thrownBy {
        Await.result(resultFuture, Duration.Inf)
      }
    }
  }

}
