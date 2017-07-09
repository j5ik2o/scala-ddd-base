package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.cats.{ FreeIODeleteFeature, FreeIORepositoryFeature }
import com.github.j5ik2o.scala.ddd.functional.example.slick3.UserFutureDriver

import scala.concurrent.{ ExecutionContext, Future }

class UserRepositoryByFuture(val driver: UserFutureDriver) extends FreeIORepositoryFeature with FreeIODeleteFeature {
  override type DriverType      = UserFutureDriver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type EvalType[A]     = Future[A]
  override type IOContextType   = ExecutionContext
}
