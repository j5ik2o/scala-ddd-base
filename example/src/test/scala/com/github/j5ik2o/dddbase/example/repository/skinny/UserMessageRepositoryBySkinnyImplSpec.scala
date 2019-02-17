package com.github.j5ik2o.dddbase.example.repository.skinny
import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.example.model.{ Status, UserMessage, UserMessageId }
import com.github.j5ik2o.dddbase.example.repository.util.{ FlywayWithMySQLSpecSupport, SkinnySpecSupport }
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{ FreeSpecLike, Matchers }
import scalikejdbc.AutoSession

class UserMessageRepositoryBySkinnyImplSpec
    extends FreeSpecLike
    with FlywayWithMySQLSpecSupport
    with SkinnySpecSupport
    with Matchers {
  override val tables: Seq[String] = Seq("user_message")

  val userMessage = UserMessage(
    id = UserMessageId(1L, 1L),
    status = Status.Active,
    message = "ABC",
    createdAt = ZonedDateTime.now(),
    updatedAt = None
  )
  val userMessages = for (idValue <- 1L to 10L)
    yield
      UserMessage(
        id = UserMessageId(idValue, idValue),
        status = Status.Active,
        message = "ABC",
        createdAt = ZonedDateTime.now(),
        updatedAt = None
      )
  val repository = new UserMessageRepositoryBySkinnyImpl

  "UserMessageRepositoryBySkinnyImpl" - {
    "store" in {
      val result = (for {
        _ <- repository.store(userMessage)
        r <- repository.resolveById(userMessage.id)
      } yield r).run(AutoSession).runToFuture.futureValue

      result shouldBe userMessage
    }
    "storeMulti" in {
      val result = (for {
        _ <- repository.storeMulti(userMessages)
        r <- repository.resolveMulti(userMessages.map(_.id))
      } yield r).run(AutoSession).runToFuture.futureValue

      result shouldBe userMessages
    }
  }
}
