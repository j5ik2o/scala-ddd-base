package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateSingleWriter
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateSingleWriteFeature extends AggregateSingleWriter[RIO] with AggregateBaseWriteFeature {

  override def store(aggregate: AggregateType): RIO[Long] = {
    for {
      record <- convertToRecord(aggregate)
      result <- ReaderT[Task, DBSession, Long] { implicit dbSession =>
        Task {
          dao.createOrUpdate(record)
        }
      }
    } yield result
  }

}
