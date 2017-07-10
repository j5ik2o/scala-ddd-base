package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.AggregateFutureIOContext
import com.github.j5ik2o.scala.ddd.functional.example.driver.UserFutureStorageDriver

import scala.concurrent.Future

trait UserRepositoryAsync extends UserRepositoryBase { self =>
  override type IOContextType <: AggregateFutureIOContext
  override type DriverType <: UserFutureStorageDriver {
    type AggregateIdType = self.AggregateIdType
    type AggregateType   = self.AggregateType
    type IOContextType   = self.IOContextType
  }
  override type EvalType[A] = Future[A]
}
