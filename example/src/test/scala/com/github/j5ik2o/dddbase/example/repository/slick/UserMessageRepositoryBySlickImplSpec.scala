package com.github.j5ik2o.dddbase.example.repository.slick

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model.{ Status, UserMessage, UserMessageId }
import com.github.j5ik2o.dddbase.example.repository.{ IdGenerator, SpecSupport }
import com.github.j5ik2o.dddbase.example.repository.util.{ FlywayWithMySQLSpecSupport, Slick3SpecSupport }
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{ FreeSpecLike, Matchers }

class UserMessageRepositoryBySlickImplSpec
    extends FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with Slick3SpecSupport
    with Matchers
    with SpecSupport {

  override val tables: Seq[String] = Seq("user_message")

  val userMessage = UserMessage(
    id = UserMessageId(IdGenerator.generateIdValue, IdGenerator.generateIdValue),
    status = Status.Active,
    message = "ABC",
    createdAt = ZonedDateTime.now(),
    updatedAt = None
  )

  val userMessages = for (idx <- 1L to 10L)
    yield
      UserMessage(
        id = UserMessageId(IdGenerator.generateIdValue, IdGenerator.generateIdValue),
        status = Status.Active,
        message = s"ABC${idx}",
        createdAt = ZonedDateTime.now(),
        updatedAt = None
      )

  "UserMessageRepositoryBySlickImpl" - {
    "store" in {
      val repository = new UserMessageRepositoryBySlickImpl(dbConfig.profile, dbConfig.db)
      val result = (for {
        _ <- repository.store(userMessage)
        r <- repository.resolveById(userMessage.id)
      } yield r).runToFuture.futureValue

      result shouldBe userMessage
    }
    "storeMulti" in {
      val repository = new UserMessageRepositoryBySlickImpl(dbConfig.profile, dbConfig.db)
      val result = (for {
        _ <- repository.storeMulti(userMessages)
        r <- repository.resolveMulti(userMessages.map(_.id))
      } yield r).runToFuture.futureValue

      sameAs(result, userMessages) shouldBe true
    }
  }
}
