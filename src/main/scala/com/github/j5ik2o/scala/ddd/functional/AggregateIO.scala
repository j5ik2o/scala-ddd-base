package com.github.j5ik2o.scala.ddd.functional

trait AggregateIO { self =>
  type IdValueType = Long
  type IdType <: AggregateId {
    type IdValueType = self.IdValueType
  }
  type AggregateType <: Aggregate {
    type IdType = self.IdType
  }
}
