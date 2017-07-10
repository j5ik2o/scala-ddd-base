package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.{ AggregateDeletable, AggregateRepositoryDSL, Delete }

trait FreeIODeleteFeature extends FreeIOBaseFeature with FreeIOWriteFeature with AggregateDeletable {
  override def deleteById(id: AggregateIdType)(implicit ctx: IOContextType): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Delete(id))
}
