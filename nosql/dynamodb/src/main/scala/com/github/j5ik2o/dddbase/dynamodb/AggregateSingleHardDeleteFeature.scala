package com.github.j5ik2o.dddbase.dynamodb
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleWriter }

trait AggregateSingleHardDeleteFeature extends AggregateSingleHardDeletable[RIO] with AggregateBaseWriteFeature {
  this: AggregateSingleWriter[RIO] =>

  override def hardDelete(id: IdType): RIO[Long] = dao.delete(id.value.toString)

}
