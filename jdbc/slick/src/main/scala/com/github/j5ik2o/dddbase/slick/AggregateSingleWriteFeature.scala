package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateSingleWriter
import monix.eval.Task

trait AggregateSingleWriteFeature extends AggregateSingleWriter[Task] with AggregateBaseWriteFeature {

  override def store(aggregate: AggregateType): Task[Long] =
    for {
      record <- convertToRecord(aggregate)
      result <- Task.deferFutureAction { implicit ec =>
        import profile.api._
        db.run(dao.insertOrUpdate(record)).map(_.toLong)
      }
    } yield result
}
