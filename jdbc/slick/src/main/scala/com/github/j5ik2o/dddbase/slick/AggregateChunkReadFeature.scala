package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.{ AggregateChunkReader, AggregatesChunk }
import monix.eval.Task

trait AggregateChunkReadFeature extends AggregateChunkReader[Task] with AggregateBaseReadFeature {

  import profile.api._

  override def resolveMultiWithOffsetLimit(offset: Option[Long], limit: Long): Task[AggregatesChunk[AggregateType]] = {
    val index = offset.map(_.toInt).getOrElse(0)
    for {
      results <- Task.deferFuture {
        db.run(dao.drop(index).take(limit).result)
      }
      aggregates <- Task.traverse(results)(convertToAggregate(_))
    } yield AggregatesChunk(index, aggregates)
  }

}
