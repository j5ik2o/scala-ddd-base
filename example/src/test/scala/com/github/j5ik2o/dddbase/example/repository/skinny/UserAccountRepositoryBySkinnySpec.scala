package com.github.j5ik2o.dddbase.example.repository.skinny

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.util.{ FlywayWithMySQLSpecSupport, SkinnySpecSupport }
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{ FreeSpecLike, Matchers }
import scalikejdbc.AutoSession

class UserAccountRepositoryBySkinnySpec
    extends FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with SkinnySpecSupport
    with Matchers {

  val repository                   = UserAccountRepository.bySkinny
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

  "UserAccountRepositoryBySkinny" - {
    "store" in {
      val result = (for {
        _ <- repository.store(userAccount)
        r <- repository.resolveById(userAccount.id)
      } yield r).run(AutoSession).runAsync.futureValue

      result shouldBe userAccount
    }
    "storeMulti" in {
      val result = (for {
        _ <- repository.storeMulti(userAccounts)
        r <- repository.resolveMulti(userAccounts.map(_.id))
      } yield r).run(AutoSession).runAsync.futureValue

      result shouldBe userAccounts
    }

  }

}
