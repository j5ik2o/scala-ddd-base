package com.github.j5ik2o.scala.ddd.functional

trait AggregateId {
  type IdValueType
  type AggregateType <: Aggregate { type IdType = this.type }
  val value: IdValueType
}
