package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiWriter
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateMultiWriteFeature extends AggregateMultiWriter[RIO] with AggregateBaseWriteFeature {

  override def storeMulti(aggregates: Seq[AggregateType]): RIO[Long] =
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
