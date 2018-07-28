package com.github.j5ik2o.dddbase

trait AggregateMultiSoftDeletable[M[_]] {
  this: AggregateMultiWriter[M] =>

  def softDeleteMulti(ids: Seq[IdType]): M[Long]

}
