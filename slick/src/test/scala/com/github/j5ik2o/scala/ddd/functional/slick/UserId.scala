package com.github.j5ik2o.scala.ddd.functional.slick

import com.github.j5ik2o.scala.ddd.functional.AggregateId

case class UserId(value: Long) extends AggregateId {
  override type AggregateType = User
  override type IdValueType   = Long
}
