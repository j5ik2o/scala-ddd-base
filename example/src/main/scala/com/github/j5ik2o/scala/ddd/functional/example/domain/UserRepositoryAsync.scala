package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.AggregateFutureIOContext
import com.github.j5ik2o.scala.ddd.functional.example.driver.UserFutureDriver

import scala.concurrent.Future

trait UserRepositoryAsync extends UserRepository { self =>
  override type IOContextType <: AggregateFutureIOContext
  override type DriverType <: UserFutureDriver {
    type AggregateIdType = self.AggregateIdType
    type AggregateType   = self.AggregateType
    type IOContextType   = self.IOContextType
  }
  override type EvalType[A] = Future[A]
}
