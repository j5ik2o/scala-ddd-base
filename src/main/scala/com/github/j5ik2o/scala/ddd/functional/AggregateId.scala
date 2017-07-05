package com.github.j5ik2o.scala.ddd.functional

trait AggregateId {
  type IdType
  type AggregateType <: Aggregate
  val value: IdType
}
