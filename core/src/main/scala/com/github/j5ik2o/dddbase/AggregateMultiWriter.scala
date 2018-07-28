package com.github.j5ik2o.dddbase

trait AggregateMultiWriter[M[_]] extends AggregateIO[M] {
  def storeMulti(aggregates: Seq[AggregateType]): M[Long]

}
