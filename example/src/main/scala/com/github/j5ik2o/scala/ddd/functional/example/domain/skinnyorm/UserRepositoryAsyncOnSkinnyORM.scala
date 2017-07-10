package com.github.j5ik2o.scala.ddd.functional.example.domain.skinnyorm

import com.github.j5ik2o.scala.ddd.functional.example.domain.UserRepositoryAsync
import com.github.j5ik2o.scala.ddd.functional.example.driver.skinnyorm.UserSkinnyORMFutureStorageDriver
import com.github.j5ik2o.scala.ddd.functional.skinnyorm.SkinnyORMFutureIOContext

class UserRepositoryAsyncOnSkinnyORM(val driver: UserSkinnyORMFutureStorageDriver) extends UserRepositoryAsync {
  override type IOContextType = SkinnyORMFutureIOContext
  override type DriverType    = UserSkinnyORMFutureStorageDriver
}
