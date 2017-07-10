package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.{ AggregateReader, AggregateRepositoryDSL, ResolveById }

trait FreeIOReadFeature extends FreeIOBaseFeature with AggregateReader {

  override type SingleResultType[A] = Option[A]

  override def resolveBy(
      id: AggregateIdType
  )(implicit ctx: IOContextType): Free[AggregateRepositoryDSL, SingleResultType[AggregateType]] =
    Free.liftF[AggregateRepositoryDSL, SingleResultType[AggregateType]](ResolveById(id))

}
