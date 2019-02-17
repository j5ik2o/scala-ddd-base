package com.github.j5ik2o.dddbase.example.repository.slick
import com.github.j5ik2o.dddbase.example.dao.slick.UserMessageComponent
import com.github.j5ik2o.dddbase.example.model.{ Status, UserMessage, UserMessageId }
import com.github.j5ik2o.dddbase.example.repository.{ BySlick, UserMessageRepository }
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.slick._
import monix.eval.Task
import slick.jdbc.JdbcProfile
import slick.lifted.Rep

abstract class AbstractUserMessageRepositoryBySlick(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
    extends UserMessageRepository[BySlick]
    with AggregateSingleReadFeature
    with AggregateMultiReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiWriteFeature
    with UserMessageComponent {

  override type RecordType = UserMessageRecord
  override type TableType  = UserMessages
  override protected val dao = UserMessageDao

  override protected def byCondition(id: IdType): TableType => Rep[Boolean] = { v =>
    import profile.api._
    v.userId === id.userId && v.messageId === id.messageId
  }

  override protected def byConditions(ids: Seq[IdType]): TableType => Rep[Boolean] = { v =>
    import profile.api._
    ids
      .map { id =>
        v.userId === id.userId && v.messageId === id.messageId
      }
      .reduceLeft(_ || _)
  }

  override protected def convertToAggregate: UserMessageRecord => RIO[UserMessage] = { record =>
    Task.pure {
      UserMessage(
        id = UserMessageId(record.userId, record.messageId),
        status = Status.withName(record.status),
        message = record.message,
        createdAt = record.createdAt,
        updatedAt = record.updatedAt
      )
    }
  }

  override protected def convertToRecord: UserMessage => RIO[UserMessageRecord] = { aggregate =>
    Task.pure {
      UserMessageRecord(
        messageId = aggregate.id.messageId,
        userId = aggregate.id.userId,
        status = aggregate.status.entryName,
        message = aggregate.message,
        createdAt = aggregate.createdAt,
        updatedAt = aggregate.updatedAt
      )
    }
  }

}
