package com.github.j5ik2o.scala.ddd.functional.driver

import com.github.j5ik2o.scala.ddd.functional.{ Aggregate, AggregateId }

trait AggregateIO { self =>
  type IdValueType
  type AggregateIdType <: AggregateId {
    type AggregateType = self.AggregateType
    type IdValueType   = self.IdValueType
  }
  type AggregateType <: Aggregate {
    type IdType        = self.AggregateIdType
    type AggregateType = self.AggregateType
  }
  type DSL[_]
  type IOContextType
}
