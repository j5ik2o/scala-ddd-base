package com.github.j5ik2o.dddbase

trait AggregateSingleWriter[M[_]] extends AggregateIO[M] {

  def store(aggregate: AggregateType): M[Long]

}
