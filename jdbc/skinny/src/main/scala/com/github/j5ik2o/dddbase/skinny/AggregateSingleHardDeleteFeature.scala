package com.github.j5ik2o.dddbase.skinny

import com.github.j5ik2o.dddbase.{AggregateSingleHardDeletable, AggregateSingleWriter}
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import cats.data.ReaderT
import monix.eval.Task

trait AggregateSingleHardDeleteFeature extends AggregateSingleHardDeletable[RIO] with AggregateBaseWriteFeature {
  this: AggregateSingleWriter[RIO] =>

  override def hardDelete(id: IdType): RIO[Long] = ReaderT { implicit dbSession =>
    Task { dao.deleteById(id.value).toLong }
  }

}
