package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import cats.data.Kleisli
import com.github.j5ik2o.scala.ddd.functional.cats.driver.FreeIOEvaluator
import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickDBIOStorageDriver

import scala.concurrent.ExecutionContext

case class UserSlickDBIOEvaluator(override val driver: UserSlickDBIOStorageDriver) extends FreeIOEvaluator {
  override type EvalType[A]     = Kleisli[driver.profile.api.DBIO, ExecutionContext, A]
  override type DriverType      = UserSlickDBIOStorageDriver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
}
