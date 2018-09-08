package com.github.j5ik2o.dddbase.example.repository.airframe
import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.BySkinny
import com.github.j5ik2o.dddbase.example.repository.util.{FlywayWithMySQLSpecSupport, SkinnySpecSupport}
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{FreeSpecLike, Matchers}
import scalikejdbc.AutoSession
import wvlet.airframe._

class AirframeSpec extends FreeSpecLike
  with FlywayWithMySQLSpecSupport
  with SkinnySpecSupport
  with Matchers {

  override val tables: Seq[String] = Seq("user_account")

  val design = newDesign.bind[UserAccountRepository[BySkinny]].toInstance(UserAccountRepository.bySkinny)

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

  "Airframe" - {
    "store and resolveById" in {
      design.withSession{ session =>
        val repository = session.build[UserAccountRepository[BySkinny]]
        val result = (for {
          _ <- repository.store(userAccount)
          r <- repository.resolveById(userAccount.id)
        } yield r).run(AutoSession).runAsync.futureValue
        result shouldBe userAccount
      }
    }
  }



}
