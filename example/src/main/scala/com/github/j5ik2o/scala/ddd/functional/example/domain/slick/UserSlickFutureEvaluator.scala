package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import com.github.j5ik2o.scala.ddd.functional.cats.FreeIOEvaluator
import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickFutureStorageDriver
import com.github.j5ik2o.scala.ddd.functional.slick.SlickFutureIOContext

import scala.concurrent.Future

case class UserSlickFutureEvaluator(override val driver: UserSlickFutureStorageDriver) extends FreeIOEvaluator {
  override type EvalType[A]     = Future[A]
  override type DriverType      = UserSlickFutureStorageDriver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type IOContextType   = SlickFutureIOContext
}
