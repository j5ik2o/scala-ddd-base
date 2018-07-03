package com.github.j5ik2o.dddbase

trait AggregateMultiSoftDeletable[M[_]] {
  this: AggregateIO[M] =>

  def softDeleteMulti(ids: Seq[IdType]): M[Long]

}
