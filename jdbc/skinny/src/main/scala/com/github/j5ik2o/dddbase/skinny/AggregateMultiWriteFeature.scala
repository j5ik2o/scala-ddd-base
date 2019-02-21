package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiWriter
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateMultiWriteFeature
    extends AggregateMultiWriter[ReaderT[Task, DBSession, ?]]
    with AggregateBaseWriteFeature {

  override def storeMulti(aggregates: Seq[AggregateType]): ReaderT[Task, DBSession, Long] =
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
