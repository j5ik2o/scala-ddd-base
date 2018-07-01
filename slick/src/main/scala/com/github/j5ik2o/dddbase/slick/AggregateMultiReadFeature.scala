package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateMultiReader
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateMultiReadFeature extends AggregateMultiReader[RIO] with AggregateBaseReadFeature {
  import profile.api._

  override def resolveMulti(ids: Seq[IdType]): RIO[Seq[AggregateType]] =
    Task
      .deferFutureAction { implicit ec =>
        db.run(dao.filter(byConditions(ids)).result)
      }
      .flatMap { records =>
        Task.traverse(records)(convertToAggregate)
      }

}
