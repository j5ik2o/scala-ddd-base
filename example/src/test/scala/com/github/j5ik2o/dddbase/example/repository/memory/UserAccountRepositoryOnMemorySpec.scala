package com.github.j5ik2o.dddbase.example.repository.memory

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.util.ScalaFuturesSupportSpec
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FreeSpec, Matchers }
import scala.concurrent.duration._

class UserAccountRepositoryOnMemorySpec extends FreeSpec with ScalaFutures with ScalaFuturesSupportSpec with Matchers {

  val repository = UserAccountRepository.onMemory(expireAfterWrite = Some(5 minutes))

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
