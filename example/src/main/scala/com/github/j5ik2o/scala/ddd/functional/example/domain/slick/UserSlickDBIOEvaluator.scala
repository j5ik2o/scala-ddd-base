package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import com.github.j5ik2o.scala.ddd.functional.cats.driver.FreeIOEvaluator
import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickDBIOStorageDriver
import com.github.j5ik2o.scala.ddd.functional.slick.SlickFutureIOContext

case class UserSlickDBIOEvaluator(override val driver: UserSlickDBIOStorageDriver) extends FreeIOEvaluator {
  override type EvalType[A]     = driver.profile.api.DBIO[A]
  override type DriverType      = UserSlickDBIOStorageDriver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type IOContextType   = SlickFutureIOContext
}
