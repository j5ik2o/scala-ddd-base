package com.github.j5ik2o.scala.ddd.functional.example.domain.skinnyorm

import com.github.j5ik2o.scala.ddd.functional.cats.FreeIOEvaluator
import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.example.driver.skinnyorm.UserSkinnyORMFutureStorageDriver
import com.github.j5ik2o.scala.ddd.functional.skinnyorm.SkinnyORMFutureIOContext

import scala.concurrent.Future

case class UserSkinnyORMFutureEvaluator(override val driver: UserSkinnyORMFutureStorageDriver)
    extends FreeIOEvaluator {
  override type EvalType[A]     = Future[A]
  override type DriverType      = UserSkinnyORMFutureStorageDriver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type IOContextType   = SkinnyORMFutureIOContext
}
