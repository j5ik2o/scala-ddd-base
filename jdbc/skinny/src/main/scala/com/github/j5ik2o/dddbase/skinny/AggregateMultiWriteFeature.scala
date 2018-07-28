package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateMultiWriter, NullStoreOption, StoreOption }
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateMultiWriteFeature extends AggregateMultiWriter[RIO] with AggregateBaseWriteFeature {

  override type SMO = StoreOption
  override val defaultStoreMultiOption: StoreOption = NullStoreOption

  override def storeMulti(aggregates: Seq[AggregateType], storeMultiOption: SMO): RIO[Long] =
    ReaderT[Task, DBSession, Long] { dbSession =>
      for {
        records <- Task.traverse(aggregates) { aggregate =>
          convertToRecord(aggregate)(dbSession)
        }
        result <- Task
          .traverse(records) { record =>
            Task { dao.createOrUpdate(record) }
          }
          .map(_.count(_ > 0))
      } yield result
    }

}
