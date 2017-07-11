package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import cats.data.Kleisli
import com.github.j5ik2o.scala.ddd.functional.cats.driver.FreeIOEvaluator
import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickFutureStorageDriver

import scala.concurrent.{ ExecutionContext, Future }

case class UserSlickFutureEvaluator(override val driver: UserSlickFutureStorageDriver) extends FreeIOEvaluator {
  override type EvalType[A]     = Kleisli[Future, ExecutionContext, A]
  override type DriverType      = UserSlickFutureStorageDriver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
}
