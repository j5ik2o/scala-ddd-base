package com.github.j5ik2o.dddbase.example.repository.slick

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.util.{FlywayWithMySQLSpecSupport, Slick3SpecSupport}
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{FreeSpecLike, Matchers}

class UserAccountRepositoryBySlickSpec
    extends FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with Slick3SpecSupport
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

  "UserAccountRepositoryBySlick" - {
    "store" in {
      val repository = UserAccountRepository.bySlick(dbConfig.profile, dbConfig.db)
      val result = (for {
        _ <- repository.store(userAccount)
        r <- repository.resolveById(userAccount.id)
      } yield r).runAsync.futureValue

      result shouldBe userAccount
    }
    "storeMulti" in {
      val repository = UserAccountRepository.bySlick(dbConfig.profile, dbConfig.db)
      val result = (for {
        _ <- repository.storeMulti(userAccounts)
        r <- repository.resolveMulti(userAccounts.map(_.id))
      } yield r).runAsync.futureValue

      result shouldBe userAccounts
    }
  }
}
