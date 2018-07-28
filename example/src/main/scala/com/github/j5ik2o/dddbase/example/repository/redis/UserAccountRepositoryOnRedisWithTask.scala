package com.github.j5ik2o.dddbase.example.repository.redis
import akka.actor.ActorSystem
import cats.data.ReaderT
import com.github.j5ik2o.dddbase.example.dao.redis.UserAccountComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.OnRedisWithTask
import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.redis._
import monix.eval.Task

class UserAccountRepositoryOnRedisWithTask()(implicit system: ActorSystem)
    extends UserAccountRepository[OnRedisWithTask]
    with AggregateSingleReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiReadFeature
    with AggregateMultiWriteFeature
    with AggregateSingleSoftDeleteFeature
    with UserAccountComponent {

  override type RecordType = UserAccountRecord
  override type DaoType    = UserAccountDao

  override protected val dao = UserAccountDao()

  override protected def convertToAggregate: UserAccountRecord => RIO[UserAccount] = { record =>
    ReaderT { _ =>
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
  }

  override protected def convertToRecord: UserAccount => RIO[UserAccountRecord] = { aggregate =>
    ReaderT { _ =>
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

}
