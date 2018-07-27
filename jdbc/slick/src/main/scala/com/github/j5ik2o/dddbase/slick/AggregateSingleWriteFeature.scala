package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{AggregateSingleWriter, NullStoreOption, StoreOption}
import monix.eval.Task

trait AggregateSingleWriteFeature extends AggregateSingleWriter[RIO] with AggregateBaseWriteFeature {

  override type SO = StoreOption
  override val defaultStoreOption: SO = NullStoreOption

  override def store(aggregate: AggregateType, storeOption: SO): RIO[Long] =
    for {
      record <- convertToRecord(aggregate)
      result <- Task.deferFutureAction { implicit ec =>
        import profile.api._
        db.run(dao.insertOrUpdate(record)).map(_.toLong)
      }
    } yield result
}
