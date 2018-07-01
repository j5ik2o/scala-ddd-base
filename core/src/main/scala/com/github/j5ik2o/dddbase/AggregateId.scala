package com.github.j5ik2o.dddbase

trait AggregateId {
  type IdType
  val value: IdType
}
