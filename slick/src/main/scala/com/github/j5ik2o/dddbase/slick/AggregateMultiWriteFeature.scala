package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateMultiWriter
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateMultiWriteFeature extends AggregateMultiWriter[RIO] with AggregateBaseWriteFeature {
  import profile.api._

  override def storeMulti(aggregates: Seq[AggregateType]): RIO[Int] =
    Task.unit
      .flatMap { _ =>
        Task.traverse(aggregates)(convertToRecord)
      }
      .flatMap { records =>
        Task.deferFutureAction { implicit ec =>
          db.run(DBIO.sequence(records.foldLeft(Seq.empty[DBIO[Int]]) {
              case (result, record) =>
                result :+ dao.insertOrUpdate(record)
            }))
            .map(_.sum)
        }
      }
}
