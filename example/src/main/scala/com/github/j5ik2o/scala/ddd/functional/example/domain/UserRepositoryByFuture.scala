package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickFutureDriver
import com.github.j5ik2o.scala.ddd.functional.slick.SlickFutureIOContext

import scala.concurrent.Future

class UserRepositoryByFuture(val driver: UserSlickFutureDriver) extends UserRepository {
  override type IOContextType = SlickFutureIOContext
  override type DriverType    = UserSlickFutureDriver
  override type EvalType[A]   = Future[A]
}
