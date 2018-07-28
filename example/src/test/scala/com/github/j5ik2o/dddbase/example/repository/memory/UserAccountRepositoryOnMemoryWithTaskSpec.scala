package com.github.j5ik2o.dddbase.example.repository.memory

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.OnMemoryWithTask
import com.github.j5ik2o.dddbase.example.repository.util.ScalaFuturesSupportSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FreeSpec, Matchers }
import monix.execution.Scheduler.Implicits.global

class UserAccountRepositoryOnMemoryWithTaskSpec
    extends FreeSpec
    with ScalaFutures
    with ScalaFuturesSupportSpec
    with Matchers {

  val repository = UserAccountRepository.onMemoryWithTask()

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

  "UserAccountRepositoryOnMemoryWithTask" - {
    "store" in {
      val result = (for {
        _ <- repository.store(userAccount)
        r <- repository.resolveById(userAccount.id)
      } yield r).runAsync.futureValue

      result shouldBe userAccount
    }
    "storeMulti" in {
      val result = (for {
        _ <- repository.storeMulti(userAccounts)
        r <- repository.resolveMulti(userAccounts.map(_.id))
      } yield r).runAsync.futureValue

      result shouldBe userAccounts
    }
  }

}
