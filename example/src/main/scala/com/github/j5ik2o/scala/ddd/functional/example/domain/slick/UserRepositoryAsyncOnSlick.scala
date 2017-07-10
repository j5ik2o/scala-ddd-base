package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import com.github.j5ik2o.scala.ddd.functional.example.domain.UserRepositoryAsync
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickFutureDriver
import com.github.j5ik2o.scala.ddd.functional.slick.SlickFutureIOContext

class UserRepositoryAsyncOnSlick(val driver: UserSlickFutureDriver) extends UserRepositoryAsync {
  override type IOContextType = SlickFutureIOContext
  override type DriverType    = UserSlickFutureDriver
}
