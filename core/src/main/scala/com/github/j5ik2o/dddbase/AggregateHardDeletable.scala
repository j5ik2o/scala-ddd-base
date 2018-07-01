package com.github.j5ik2o.dddbase

trait AggregateHardDeletable[M[_]] { this: AggregateSingleWriter[M] =>

  def hardDelete(id: IdType): M[Unit]

}
