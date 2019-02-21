package com.github.j5ik2o.dddbase.example.repository.memory

import com.github.j5ik2o.dddbase.example.dao.memory.UserAccountComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.{ OnMemory, UserAccountRepository }
import com.github.j5ik2o.dddbase.memory._
import com.google.common.base.Ticker
import monix.eval.Task

import scala.concurrent.duration.Duration

class UserAccountRepositoryOnMemory(concurrencyLevel: Option[Int] = None,
                                    expireAfterAccess: Option[Duration] = None,
                                    expireAfterWrite: Option[Duration] = None,
                                    initialCapacity: Option[Int] = None,
                                    maximumSize: Option[Int] = None,
                                    maximumWeight: Option[Int] = None,
                                    recordStats: Option[Boolean] = None,
                                    refreshAfterWrite: Option[Duration] = None,
                                    softValues: Option[Boolean] = None,
                                    ticker: Option[Ticker] = None,
                                    weakKeys: Option[Boolean] = None,
                                    weakValues: Option[Boolean] = None)
    extends UserAccountRepository[OnMemory]
    with AggregateSingleReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiWriteFeature
    with AggregateMultiReadFeature
    with AggregateSingleSoftDeleteFeature
    with AggregateMultiSoftDeleteFeature
    with UserAccountComponent {

  override type RecordType = UserAccountRecord
  override type DaoType    = UserAccountDao

  override protected val dao: UserAccountDao =
    new UserAccountDao(
      concurrencyLevel = concurrencyLevel,
      expireAfterAccess = expireAfterAccess,
      expireAfterWrite = expireAfterWrite,
      initialCapacity = initialCapacity,
      maximumSize = maximumSize,
      maximumWeight = maximumWeight,
      recordStats = recordStats,
      refreshAfterWrite = refreshAfterWrite,
      softValues = softValues,
      ticker = ticker,
      weakKeys = weakKeys,
      weakValues = weakValues
    )

  override protected def convertToAggregate: UserAccountRecord => Task[UserAccount] = { record =>
    Task.pure {
      UserAccount(
        id = UserAccountId(record.id.toLong),
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

  override protected def convertToRecord: UserAccount => Task[UserAccountRecord] = { aggregate =>
    Task.pure {
      UserAccountRecord(
        id = aggregate.id.value.toString,
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
