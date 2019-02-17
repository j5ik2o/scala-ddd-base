package com.github.j5ik2o.dddbase.example.model
import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.{ Aggregate, AggregateId }

import scala.reflect.{ classTag, ClassTag }

case class UserMessageId(userId: Long, messageId: Long) extends AggregateId {
  override type IdType = (Long, Long)
  override val value = (userId, messageId)
}

case class UserMessage(id: UserMessageId,
                       status: Status,
                       message: String,
                       createdAt: ZonedDateTime,
                       updatedAt: Option[ZonedDateTime])
    extends Aggregate {
  override type IdType        = UserMessageId
  override type AggregateType = UserMessage
  override protected val tag: ClassTag[UserMessage] = classTag[UserMessage]
}
