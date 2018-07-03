package com.github.j5ik2o.dddbase

trait AggregateSingleSoftDeletable[M[_]] { this: AggregateIO[M] =>

  def softDelete(id: IdType): M[Long]

}
