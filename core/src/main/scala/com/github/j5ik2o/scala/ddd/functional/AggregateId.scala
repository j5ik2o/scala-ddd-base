package com.github.j5ik2o.scala.ddd.functional

trait AggregateId {
  type IdValueType
  type AggregateType <: Aggregate
  val value: IdValueType
}
