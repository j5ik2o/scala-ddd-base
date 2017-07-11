package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.{ AggregateDeletable, AggregateRepositoryDSL }

trait FreeIODeleteFeature extends FreeIOBaseFeature with FreeIOWriteFeature with AggregateDeletable {

  import AggregateRepositoryDSL._

  override def deleteById(id: AggregateIdType): DSL[Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Delete(id))

}
