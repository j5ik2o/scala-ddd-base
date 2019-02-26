package com.github.j5ik2o.dddbase.example.repository.free

import java.time.ZonedDateTime

import cats.free.Free
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ IdGenerator, SpecSupport, UserAccountRepository }
import com.github.j5ik2o.dddbase.example.repository.util.{ FlywayWithMySQLSpecSupport, SkinnySpecSupport }
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{ FreeSpecLike, Matchers }
import scalikejdbc.AutoSession

class UserAccountRepositoryByFreeSpec
    extends FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with SkinnySpecSupport
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

  "UserAccountRepositoryByFree" - {
    "store" in {
      val program: Free[UserRepositoryDSL, UserAccount] = for {
        _      <- UserAccountRepositoryByFree.store(userAccount)
        result <- UserAccountRepositoryByFree.resolveById(userAccount.id)
      } yield result
      val skinny     = UserAccountRepository.bySkinny
      val evalResult = UserAccountRepositoryByFree.evaluate(skinny)(program)
      val result     = evalResult.run(AutoSession).runToFuture.futureValue
      result shouldBe userAccount
    }
  }
}
