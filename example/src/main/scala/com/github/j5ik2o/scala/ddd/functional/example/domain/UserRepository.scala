package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.cats.{ FreeIODeleteFeature, FreeIORepositoryFeature }

object UserRepository extends FreeIORepositoryFeature with FreeIODeleteFeature {
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
}
