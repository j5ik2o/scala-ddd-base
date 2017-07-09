package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.cats.{ FreeIODeleteFeature, FreeIORepositoryFeature }
import com.github.j5ik2o.scala.ddd.functional.example.slick3.UserDBIODriver

import scala.concurrent.ExecutionContext

class UserRepositoryByDBIO(val driver: UserDBIODriver) extends FreeIORepositoryFeature with FreeIODeleteFeature {
  override type DriverType      = UserDBIODriver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  import driver.profile.api._
  override type EvalType[A]   = DBIO[A]
  override type IOContextType = ExecutionContext
}
