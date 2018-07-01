package com.github.j5ik2o.dddbase

trait AggregateIO[M[_]] {
  type AggregateType <: Aggregate
  type IdType <: AggregateId
}
