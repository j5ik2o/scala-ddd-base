package com.github.j5ik2o.scala.ddd.functional

trait AggregateIO {
  type AggregateType <: Aggregate
  type IdType <: AggregateId
}
