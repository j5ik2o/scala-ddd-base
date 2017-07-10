package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.cats.{ FreeIODeleteFeature, FreeIORepositoryFeature }
import com.github.j5ik2o.scala.ddd.functional.example.slick3.UserSlickFutureDriver

import scala.concurrent.{ ExecutionContext, Future }

class UserRepositoryByFuture(val driver: UserSlickFutureDriver)
    extends FreeIORepositoryFeature
    with FreeIODeleteFeature {
  override type DriverType      = UserSlickFutureDriver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type EvalType[A]     = Future[A]
  override type IOContextType   = ExecutionContext
}
