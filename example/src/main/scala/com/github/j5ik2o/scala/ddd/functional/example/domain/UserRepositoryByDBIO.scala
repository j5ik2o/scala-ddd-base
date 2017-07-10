package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickDBIODriver

import scala.concurrent.ExecutionContext

class UserRepositoryByDBIO(val driver: UserSlickDBIODriver) extends UserRepository {
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type IOContextType   = ExecutionContext
  override type DriverType      = UserSlickDBIODriver
  import driver.profile.api._
  override type EvalType[A] = DBIO[A]
}
