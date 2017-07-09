package com.github.j5ik2o.scala.ddd.functional.example.skinnyorm

import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.skinnyorm.SkinnyORMDriver

class UserSkinnyORMDriver extends SkinnyORMDriver {

  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type RecordType      = UserRecord
  override val dao = UserDao

  override protected def convertToRecord(aggregate: User): UserRecord =
    UserRecord(id = aggregate.id.value, name = aggregate.name)

  override protected def convertToAggregate(record: Option[UserRecord]): Option[User] =
    record.map(e => User(id = UserId(e.id), name = e.name))

  override protected def toNamedValues(record: UserRecord): Seq[(Symbol, Any)] = Seq(
    'name -> record.name
  )
}
