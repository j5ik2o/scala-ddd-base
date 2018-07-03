package com.github.j5ik2o.dddbase

trait AggregateSoftDeletable[M[_]] { this: AggregateIO[M] =>

  def softDelete(id: IdType): M[Long]

}
