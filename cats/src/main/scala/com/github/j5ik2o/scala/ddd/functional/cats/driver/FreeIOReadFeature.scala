package com.github.j5ik2o.scala.ddd.functional.cats.driver

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL
import com.github.j5ik2o.scala.ddd.functional.driver.AggregateReader

trait FreeIOReadFeature extends FreeIOBaseFeature with AggregateReader {

  import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL._

  override def resolveBy(
      id: AggregateIdType
  ): DSL[Option[AggregateType]] =
    Free.liftF[AggregateRepositoryDSL, Option[AggregateType]](ResolveById(id))

}
