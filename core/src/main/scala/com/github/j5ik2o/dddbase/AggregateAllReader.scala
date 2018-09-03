package com.github.j5ik2o.dddbase

trait AggregateAllReader[M[_]] extends AggregateIO[M] {

  def resolveAll: M[Seq[AggregateType]]

}
