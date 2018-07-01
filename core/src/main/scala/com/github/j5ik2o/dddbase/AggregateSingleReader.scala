package com.github.j5ik2o.dddbase

trait AggregateSingleReader[M[_]] extends AggregateIO[M] {
  def resolveById(id: IdType): M[AggregateType]
}
