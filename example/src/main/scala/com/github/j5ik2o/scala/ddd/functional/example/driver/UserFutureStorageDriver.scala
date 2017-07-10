package com.github.j5ik2o.scala.ddd.functional.example.driver

import com.github.j5ik2o.scala.ddd.functional.cats.FutureStorageDriver
import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }

trait UserFutureStorageDriver extends FutureStorageDriver {
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
}
