package com.github.j5ik2o.dddbase

trait AggregateMultiWriter[M[_]] extends AggregateIO[M] {
  type SMO
  val defaultStoreMultiOption: SMO
  def storeMulti(aggregates: Seq[AggregateType], storeMultiOption: SMO = defaultStoreMultiOption): M[Long]

}
