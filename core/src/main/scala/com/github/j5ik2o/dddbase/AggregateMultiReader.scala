package com.github.j5ik2o.dddbase

trait AggregateMultiReader[M[_]] extends AggregateIO[M] {

  def resolveMulti(ids: Seq[IdType]): M[Seq[AggregateType]]

}
