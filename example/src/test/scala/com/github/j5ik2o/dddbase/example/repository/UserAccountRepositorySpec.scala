package com.github.j5ik2o.dddbase.example.repository

import java.time.ZonedDateTime

import cats.free.Free
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.{ByFree, BySlickWithTask}
import com.github.j5ik2o.dddbase.example.repository.free.{UserAccountRepositoryOnFree, UserRepositoryDSL}
import com.github.j5ik2o.dddbase.example.repository.util.{
  FlywayWithMySQLSpecSupport,
  SkinnySpecSupport,
  Slick3SpecSupport
}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{FreeSpecLike, Matchers}
import scalikejdbc.AutoSession

class UserAccountRepositorySpec
    extends FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with Slick3SpecSupport
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

  "UserAccountRepository" - {
    "slick" - {
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
    "skinny" - {
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
    "free" - {
      "skinny" in {
        val free = UserAccountRepository[ByFree]
        val program: Free[UserRepositoryDSL, UserAccount] = for {
          _      <- free.store(userAccount)
          result <- free.resolveById(userAccount.id)
        } yield result

        val skinny     = UserAccountRepository.bySkinnyWithTask
        val evalResult = UserAccountRepositoryOnFree.evaluate(skinny)(program)
        val result     = evalResult.run(AutoSession).runAsync.futureValue
        result shouldBe userAccount
      }
      "slick" in {
        val free = UserAccountRepository[ByFree]
        val program = for {
          _      <- free.store(userAccount)
          result <- free.resolveById(userAccount.id)
        } yield result

        val slick                         = UserAccountRepository.bySlickWithTask(dbConfig.profile, dbConfig.db)
        val evalResult: Task[UserAccount] = UserAccountRepositoryOnFree.evaluate(slick)(program)
        val result                        = evalResult.runAsync.futureValue
        result shouldBe userAccount
      }
    }
  }
}
