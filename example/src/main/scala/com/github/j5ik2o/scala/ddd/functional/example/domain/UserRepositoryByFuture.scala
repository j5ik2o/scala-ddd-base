package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickFutureDriver

import scala.concurrent.{ ExecutionContext, Future }

class UserRepositoryByFuture(val driver: UserSlickFutureDriver) extends UserRepository {
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type IOContextType   = ExecutionContext
  override type EvalType[A]     = Future[A]
  override type DriverType      = UserSlickFutureDriver
}
