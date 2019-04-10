package com.github.j5ik2o.dddbase.example.model

import java.time.ZonedDateTime

import com.github.j5ik2o.dddbase.{ Aggregate, AggregateLongId }

import scala.reflect._

case class UserAccountId(value: Long) extends AggregateLongId

case class EmailAddress(value: String)

case class HashedPassword(value: String)

case class UserAccount(
    id: UserAccountId,
    status: Status,
    emailAddress: EmailAddress,
    password: HashedPassword,
    firstName: String,
    lastName: String,
    createdAt: ZonedDateTime,
    updatedAt: Option[ZonedDateTime]
) extends Aggregate {
  override type AggregateType = UserAccount
  override type IdType        = UserAccountId
  override protected val tag: ClassTag[UserAccount] = classTag[UserAccount]
}
