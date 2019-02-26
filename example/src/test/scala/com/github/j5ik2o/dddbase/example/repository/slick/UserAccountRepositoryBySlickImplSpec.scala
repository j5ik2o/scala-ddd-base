package com.github.j5ik2o.dddbase.example.repository.slick

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ IdGenerator, SpecSupport, UserAccountRepository }
import com.github.j5ik2o.dddbase.example.repository.util.{ FlywayWithMySQLSpecSupport, Slick3SpecSupport }
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{ FreeSpecLike, Matchers }

class UserAccountRepositoryBySlickImplSpec
    extends FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with Slick3SpecSupport
    with Matchers
    with SpecSupport {

  override val tables: Seq[String] = Seq("user_account")

  val userAccount = UserAccount(
    id = UserAccountId(IdGenerator.generateIdValue),
    status = Status.Active,
    emailAddress = EmailAddress("test@test.com"),
    password = HashedPassword("aaa"),
    firstName = "Junichi",
    lastName = "Kato",
    createdAt = ZonedDateTime.now,
    updatedAt = None
  )

  val userAccounts = for (idx <- 1L to 10L)
    yield
      UserAccount(
        id = UserAccountId(IdGenerator.generateIdValue),
        status = Status.Active,
        emailAddress = EmailAddress(s"user${idx}@gmail.com"),
        password = HashedPassword("aaa"),
        firstName = "Junichi",
        lastName = "Kato",
        createdAt = ZonedDateTime.now,
        updatedAt = None
      )

  "UserAccountRepositoryBySlickImpl" - {
    "store" in {
      val repository = new UserAccountRepositoryBySlickImpl(dbConfig.profile, dbConfig.db)
      val result = (for {
        _ <- repository.store(userAccount)
        r <- repository.resolveById(userAccount.id)
      } yield r).runToFuture.futureValue

      result shouldBe userAccount
    }
    "storeMulti" in {
      val repository = new UserAccountRepositoryBySlickImpl(dbConfig.profile, dbConfig.db)
      val result = (for {
        _ <- repository.storeMulti(userAccounts)
        r <- repository.resolveMulti(userAccounts.map(_.id))
      } yield r).runToFuture.futureValue

      sameAs(result, userAccounts) shouldBe true
    }
  }
}
