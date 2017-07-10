package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickDBIODriver

class UserRepositoryByDBIO(val driver: UserSlickDBIODriver) extends UserRepository {
  override type DriverType = UserSlickDBIODriver
  import driver.profile.api._
  override type EvalType[A] = DBIO[A]
}
