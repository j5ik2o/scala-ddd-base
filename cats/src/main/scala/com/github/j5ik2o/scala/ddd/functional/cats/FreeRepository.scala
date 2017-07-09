package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import cats.~>
import com.github.j5ik2o.scala.ddd.functional._

trait FreeIOBaseFeature extends AggregateIO with AggregateRepositoryAPIs {

  type DSL[A] = Free[AggregateRepositoryDSL, A]
  type EvalType[_]
  type RealizeType[_]

  def interpreter: (AggregateRepositoryDSL ~> EvalType)

  def eval[A](program: Free[AggregateRepositoryDSL, A])(implicit ctx: IOContext): EvalType[A]

  def realize[A](program: Free[AggregateRepositoryDSL, A])(implicit ctx: IOContext): RealizeType[A]

}

trait FreeIOWriteFeature extends FreeIOBaseFeature with AggregateWriter {

  override def store(aggregate: AggregateType)(implicit ctx: IOContext): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate)(ctx))

}

trait FreeIODeleteFeature extends FreeIOBaseFeature with FreeIOWriteFeature with AggregateDeletable {
  override def deleteById(id: AggregateIdType)(implicit ctx: IOContext): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Delete(id)(ctx))
}

trait FreeIOReadFeature extends FreeIOBaseFeature with AggregateReader {

  override type SingleResultType[A] = Option[A]

  override def resolveBy(
      id: AggregateIdType
  )(implicit ctx: IOContext): Free[AggregateRepositoryDSL, SingleResultType[AggregateType]] =
    Free.liftF[AggregateRepositoryDSL, SingleResultType[AggregateType]](ResolveById(id)(ctx))

}

trait FreeIORepositoryFeature extends FreeIOReadFeature with FreeIOWriteFeature
