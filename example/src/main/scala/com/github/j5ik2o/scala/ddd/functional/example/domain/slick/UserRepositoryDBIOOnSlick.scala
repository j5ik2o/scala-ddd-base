package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import com.github.j5ik2o.scala.ddd.functional.example.domain.UserRepository
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickDBIODriver
import com.github.j5ik2o.scala.ddd.functional.slick.SlickFutureIOContext

class UserRepositoryDBIOOnSlick(val driver: UserSlickDBIODriver) extends UserRepository {
  override type IOContextType = SlickFutureIOContext
  override type DriverType    = UserSlickDBIODriver
  override type EvalType[A]   = driver.profile.api.DBIO[A]
}
