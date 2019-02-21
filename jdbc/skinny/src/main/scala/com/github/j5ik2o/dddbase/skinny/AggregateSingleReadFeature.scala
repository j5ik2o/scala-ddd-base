package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateNotFoundException, AggregateSingleReader }
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateSingleReadFeature
    extends AggregateSingleReader[ReaderT[Task, DBSession, ?]]
    with AggregateBaseReadFeature {

  override def resolveById(id: IdType): ReaderT[Task, DBSession, AggregateType] =
    for {
      record <- ReaderT[Task, DBSession, RecordType] { implicit dbSession: DBSession =>
        Task {
          dao.findBy(byCondition(id)).getOrElse(throw AggregateNotFoundException(id))
        }
      }
      aggregate <- convertToAggregate(record)
    } yield aggregate

}
