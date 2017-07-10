package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import com.github.j5ik2o.scala.ddd.functional.example.domain.UserRepositoryBase
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickDBIOStorageDriver
import com.github.j5ik2o.scala.ddd.functional.slick.SlickFutureIOContext

class UserRepositoryDBIOOnSlick(val driver: UserSlickDBIOStorageDriver) extends UserRepositoryBase {
  override type IOContextType = SlickFutureIOContext
  override type DriverType    = UserSlickDBIOStorageDriver
  override type EvalType[A]   = driver.profile.api.DBIO[A]
}
