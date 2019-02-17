package com.github.j5ik2o.dddbase.example.repository.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.example.dao.skinny.UserAccountComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ BySkinny, UserAccountRepository }
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.skinny._
import monix.eval.Task
import scalikejdbc._

trait UserAccountRepositoryBySkinny
    extends UserAccountRepository[BySkinny]
    with AggregateSingleReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiReadFeature
    with AggregateMultiWriteFeature
    with UserAccountComponent {

  override type RecordIdType = Long
  override type RecordType   = UserAccountRecord
  override type DaoType      = UserAccountDao.type
  override protected val dao: UserAccountDao.type = UserAccountDao

  override protected def toRecordId(id: UserAccountId): Long = id.value

  override protected def byCondition(id: IdType): SQLSyntax        = sqls.eq(dao.defaultAlias.id, id.value)
  override protected def byConditions(ids: Seq[IdType]): SQLSyntax = sqls.in(dao.defaultAlias.id, ids.map(_.value))

  override protected def convertToAggregate: UserAccountRecord => RIO[UserAccount] = { record =>
    ReaderT { _ =>
      Task.pure {
        UserAccount(
          id = UserAccountId(record.id),
          status = Status.withName(record.status),
          emailAddress = EmailAddress(record.email),
          password = HashedPassword(record.password),
          firstName = record.firstName,
          lastName = record.lastName,
          createdAt = record.createdAt,
          updatedAt = record.updatedAt
        )
      }
    }
  }

  override protected def convertToRecord: UserAccount => RIO[UserAccountRecord] = { aggregate =>
    ReaderT { _ =>
      Task.pure {
        UserAccountRecord(
          id = aggregate.id.value,
          status = aggregate.status.entryName,
          email = aggregate.emailAddress.value,
          password = aggregate.password.value,
          firstName = aggregate.firstName,
          lastName = aggregate.lastName,
          createdAt = aggregate.createdAt,
          updatedAt = aggregate.updatedAt
        )
      }
    }
  }
}

class UserAccountRepositoryBySkinnyImpl
    extends UserAccountRepositoryBySkinny
    with AggregateSingleSoftDeleteFeature
    with AggregateMultiSoftDeleteFeature
