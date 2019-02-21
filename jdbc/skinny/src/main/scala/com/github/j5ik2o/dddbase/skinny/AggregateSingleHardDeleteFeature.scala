package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateSingleHardDeletable, AggregateSingleWriter }
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateSingleHardDeleteFeature
    extends AggregateSingleHardDeletable[ReaderT[Task, DBSession, ?]]
    with AggregateBaseWriteFeature {
  this: AggregateSingleWriter[ReaderT[Task, DBSession, ?]] =>

  override def hardDelete(id: IdType): ReaderT[Task, DBSession, Long] = ReaderT { implicit dbSession =>
    Task { dao.deleteById(toRecordId(id)).toLong }
  }

}
