package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.cats.{ FreeIODeleteFeature, FreeIORepositoryFeature }
import com.github.j5ik2o.scala.ddd.functional.example.slick3.UserSlick3Driver

import scala.concurrent.{ ExecutionContext, Future }

class UserRepository(val driver: UserSlick3Driver) extends FreeIORepositoryFeature with FreeIODeleteFeature {
  override type DriverType      = UserSlick3Driver
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  import driver.profile.api._
  override type EvalType[A]    = DBIO[A]
  override type RealizeType[A] = Future[A]
  override type IOContextType  = ExecutionContext
}
