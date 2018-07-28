package com.github.j5ik2o.dddbase.example.repository.memory
import com.github.j5ik2o.dddbase.example.dao.memory.UserAccountComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.OnMemory
import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.memory._
import monix.eval.Task

import scala.concurrent.duration.Duration

class UserAccountRepositoryOnMemory(minSize: Option[Int] = None,
                                    maxSize: Option[Int] = None,
                                    expireDuration: Option[Duration] = None,
                                    concurrencyLevel: Option[Int] = None,
                                    maxWeight: Option[Int] = None)
    extends UserAccountRepository[OnMemory]
    with AggregateSingleReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiWriteFeature
    with AggregateMultiReadFeature
    with AggregateSingleSoftDeleteFeature
    with UserAccountComponent {

  override type RecordType = UserAccountRecord
  override type DaoType    = UserAccountDao
  override protected val dao: UserAccountDao =
    UserAccountDao(minSize, maxSize, expireDuration, concurrencyLevel, maxWeight)

  override protected def convertToAggregate: UserAccountRecord => RIO[UserAccount] = { record =>
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

  override protected def convertToRecord: UserAccount => RIO[UserAccountRecord] = { aggregate =>
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
