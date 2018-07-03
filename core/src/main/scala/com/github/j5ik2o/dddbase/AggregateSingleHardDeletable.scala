package com.github.j5ik2o.dddbase

trait AggregateSingleHardDeletable[M[_]] { this: AggregateSingleWriter[M] =>

  def hardDelete(id: IdType): M[Unit]

}
