package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiReader
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateMultiReadFeature extends AggregateMultiReader[RIO] with AggregateBaseReadFeature {
  override def resolveMulti(ids: Seq[IdType]): RIO[Seq[AggregateType]] = {
    ReaderT[Task, DBSession, Seq[RecordType]] { implicit dbSession =>
      Task {
        dao.findAllByIds(ids.map(_.value): _*)
      }
    }.flatMap { results =>
      ReaderT { implicit dbSession =>
        Task.sequence(results.map(convertToAggregate(_)(dbSession)))
      }
    }
  }

}
