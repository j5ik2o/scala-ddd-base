package com.github.j5ik2o.dddbase.example.repository.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.example.dao.skinny.UserMessageComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ BySkinny, UserMessageRepository }
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.skinny._
import monix.eval.Task
import scalikejdbc.{ sqls, SQLSyntax }

trait UserMessageRepositoryBySkinny
    extends UserMessageRepository[BySkinny]
    with AggregateSingleReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiReadFeature
    with AggregateMultiWriteFeature
    with UserMessageComponent {
  override type RecordIdType = UserMessageRecordId
  override type RecordType   = UserMessageRecord
  override type DaoType      = UserMessageDao.type
  override protected val dao: UserMessageDao.type = UserMessageDao

  override protected def toRecordId(id: UserMessageId): UserMessageRecordId =
    UserMessageRecordId(id.messageId, id.userId)

  override protected def byCondition(id: IdType): SQLSyntax =
    sqls.eq(dao.column.messageId, id.messageId).and.eq(dao.column.userId, id.userId)

  override protected def byConditions(ids: Seq[IdType]): SQLSyntax =
    sqls.in((dao.column.messageId, dao.column.userId), ids.map(v => (v.messageId, v.userId)))

  override protected def convertToAggregate: UserMessageRecord => RIO[UserMessage] = { record =>
    ReaderT { _ =>
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
  }

  override protected def convertToRecord: UserMessage => RIO[UserMessageRecord] = { aggregate =>
    ReaderT { _ =>
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

}

class UserMessageRepositoryBySkinnyImpl
    extends UserMessageRepositoryBySkinny
    with AggregateSingleSoftDeleteFeature
    with AggregateMultiSoftDeleteFeature
