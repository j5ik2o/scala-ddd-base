package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.{ AggregateChunkReader, AggregatesChunk }
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateChunkReadFeature extends AggregateChunkReader[RIO] with AggregateBaseReadFeature {

  override def resolveMultiWithOffsetLimit(offset: Option[Long], limit: Long): RIO[AggregatesChunk[AggregateType]] =
    ReaderT[Task, DBSession, AggregatesChunk[AggregateType]] { implicit dbSession: DBSession =>
      val index = offset.map(_.toInt).getOrElse(0)
      for {
        results <- Task {
          dao.findAllWithLimitOffset(limit.toInt, index)
        }
        aggregates <- Task.sequence(results.map(convertToAggregate(_)(dbSession)))
      } yield AggregatesChunk(index, aggregates)
    }

}
