package com.github.j5ik2o.scala.ddd.functional.example.driver.slick3

import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.slick.{ CatsDBIOMonadInstance, SlickFutureDriver }
import slick.jdbc.JdbcProfile

class UserSlickFutureDriver(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
    extends SlickFutureDriver
    with UserDaoComponent
    with CatsDBIOMonadInstance {
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type RecordType      = UserRecord
  override type TableType       = UserDef
  override protected val dao = UserDao
  override type SingleResultType[A] = Option[A]

  override protected def convertToRecord(aggregate: User): UserRecord =
    UserRecord(id = aggregate.id.value, name = aggregate.name)

  override protected def convertToAggregate(record: Option[UserRecord]): Option[User] =
    record.map(e => User(id = UserId(e.id), name = e.name))
}
