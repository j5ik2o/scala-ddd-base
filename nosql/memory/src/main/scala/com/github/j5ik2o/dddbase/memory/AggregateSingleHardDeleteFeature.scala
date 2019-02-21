package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleWriter }
import monix.eval.Task

trait AggregateSingleHardDeleteFeature extends AggregateSingleHardDeletable[Task] with AggregateBaseWriteFeature {
  this: AggregateSingleWriter[Task] =>

  override def hardDelete(id: IdType): Task[Long] = dao.delete(id.value.toString)

}
