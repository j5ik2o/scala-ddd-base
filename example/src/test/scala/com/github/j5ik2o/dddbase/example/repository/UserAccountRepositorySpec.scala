package com.github.j5ik2o.dddbase.example.repository

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.util.{FlywayWithMySQLSpecSupport, Slick3SpecSupport}
import org.scalatest.FreeSpecLike

class UserAccountRepositorySpec extends FreeSpecLike with FlywayWithMySQLSpecSupport with Slick3SpecSupport {
  override val tables: Seq[String] = Seq("user_account")
  "UserAccountRepository" - {
    "" in {
      val repo = UserAccountRepository.ofSlick(dbConfig.profile, dbConfig.db)
      repo.store(
        UserAccount(
          id = UserAccountId(1L),
          status = Status.Active,
          emailAddress = EmailAddress("j5ik2o@gmail.com"),
          password = HashedPassword("aaa"),
          firstName = "Junichi",
          lastName = "Kato",
          createdAt = ZonedDateTime.now,
          updatedAt = None
        )
      )
    }
  }
}
