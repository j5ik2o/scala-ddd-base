package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateSingleWriter, NullStoreOption, StoreOption }
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateSingleWriteFeature extends AggregateSingleWriter[RIO] with AggregateBaseWriteFeature {

  override type SO = StoreOption
  override val defaultStoreOption: StoreOption = NullStoreOption

  override def store(aggregate: AggregateType, storeOption: StoreOption): RIO[Long] = {
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
