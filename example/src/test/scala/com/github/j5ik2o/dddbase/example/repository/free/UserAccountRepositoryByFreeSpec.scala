package com.github.j5ik2o.dddbase.example.repository.free
import java.time.ZonedDateTime

import cats.free.Free
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.ByFree
import com.github.j5ik2o.dddbase.example.repository.util.{ FlywayWithMySQLSpecSupport, SkinnySpecSupport }
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{ FreeSpecLike, Matchers }
import scalikejdbc.AutoSession

class UserAccountRepositoryByFreeSpec
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

  "UserAccountRepositoryByFree" - {
    "store" in {
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
  }
}
