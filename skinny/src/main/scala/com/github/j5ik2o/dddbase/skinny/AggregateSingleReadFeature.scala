package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{AggregateNotFoundException, AggregateSingleReader}
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateSingleReadFeature extends AggregateSingleReader[RIO] with AggregateBaseReadFeature {

  override def resolveById(id: IdType): RIO[AggregateType] =
    ReaderT[Task, DBSession, RecordType] { implicit dbSession: DBSession =>
      Task {
        dao.findById(id.value).getOrElse(throw AggregateNotFoundException(id))
      }
    }.flatMap(convertToAggregate)

}
