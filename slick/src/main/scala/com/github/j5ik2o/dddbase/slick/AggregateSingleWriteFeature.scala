package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateSingleWriter
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateSingleWriteFeature extends AggregateSingleWriter[RIO] with AggregateBaseWriteFeature {
  import profile.api._

  override def store(aggregate: AggregateType): RIO[Int] =
    convertToRecord(aggregate).flatMap { record =>
      Task.deferFuture {
        db.run(dao.insertOrUpdate(record))
      }
    }

}
