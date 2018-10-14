package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.{ AggregateChunkReader, AggregatesChunk }
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateChunkReadFeature extends AggregateChunkReader[RIO] with AggregateBaseReadFeature {

  import profile.api._

  override def resolveMultiWithOffsetLimit(offset: Option[Long], limit: Long): RIO[AggregatesChunk[AggregateType]] = {
    val index = offset.map(_.toInt).getOrElse(0)
    for {
      results <- Task.deferFuture {
        db.run(dao.drop(index).take(limit).result)
      }
      aggregates <- Task.traverse(results)(convertToAggregate(_))
    } yield AggregatesChunk(index, aggregates)
  }

}
