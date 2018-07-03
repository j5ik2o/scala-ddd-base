package com.github.j5ik2o.dddbase

trait AggregateMultiHardDeletable[M[_]] { this: AggregateMultiWriter[M] =>

  def hardDeleteMulti(ids: Seq[IdType]): M[Long]

}
