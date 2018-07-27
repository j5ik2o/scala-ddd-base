package com.github.j5ik2o.dddbase

trait AggregateSingleWriter[M[_]] extends AggregateIO[M] {
  type SO
  val defaultStoreOption: SO
  def store(aggregate: AggregateType, option: SO = defaultStoreOption): M[Long]

}
