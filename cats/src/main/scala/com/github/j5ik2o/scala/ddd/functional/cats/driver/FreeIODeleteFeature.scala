package com.github.j5ik2o.scala.ddd.functional.cats.driver

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL
import com.github.j5ik2o.scala.ddd.functional.driver.AggregateDeletable

trait FreeIODeleteFeature extends FreeIOBaseFeature with FreeIOWriteFeature with AggregateDeletable {

  import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL._

  override def deleteById(id: AggregateIdType)(implicit ec: IOContextType): DSL[Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Delete(id))

}
