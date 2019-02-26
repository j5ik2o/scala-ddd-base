package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateChunkReader, AggregatesChunk }
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateChunkReadFeature
    extends AggregateChunkReader[ReaderT[Task, DBSession, ?]]
    with AggregateBaseReadFeature {

  override def resolveMultiWithOffsetLimit(offset: Option[Long],
                                           limit: Long): ReaderT[Task, DBSession, AggregatesChunk[AggregateType]] =
    ReaderT[Task, DBSession, AggregatesChunk[AggregateType]] { implicit dbSession: DBSession =>
      val index = offset.map(_.toInt).getOrElse(0)
      for {
        results <- Task {
          dao.findAllWithLimitOffset(limit.toInt, index)
        }
        aggregates <- Task.gather(results.map(convertToAggregate(_)(dbSession)))
      } yield AggregatesChunk(index, aggregates)
    }

}
