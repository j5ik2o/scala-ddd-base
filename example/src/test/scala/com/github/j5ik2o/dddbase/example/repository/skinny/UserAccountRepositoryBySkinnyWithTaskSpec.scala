package com.github.j5ik2o.dddbase.example.repository.skinny
import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.util.{ FlywayWithMySQLSpecSupport, SkinnySpecSupport }
import org.scalatest.{ FreeSpecLike, Matchers }
import scalikejdbc.AutoSession
import monix.execution.Scheduler.Implicits.global

class UserAccountRepositoryBySkinnyWithTaskSpec
    extends FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with SkinnySpecSupport
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

  "UserAccountRepositoryBySkinnyWithTask" - {
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

}
