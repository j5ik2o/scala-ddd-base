package com.github.j5ik2o.dddbase.example.repository.slick
import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.util.{ FlywayWithMySQLSpecSupport, Slick3SpecSupport }
import org.scalatest.{ FreeSpecLike, Matchers }
import monix.execution.Scheduler.Implicits.global

class UserAccountRepositoryBySlickWithTaskSpec
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

  "UserAccountRepositoryBySlickWithTask" - {
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
}
