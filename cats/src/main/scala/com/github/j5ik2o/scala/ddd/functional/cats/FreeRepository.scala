package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import cats.~>
import com.github.j5ik2o.scala.ddd.functional.{ AggregateRepository, AggregateRepositoryAPIs }

import scala.concurrent.ExecutionContext

trait FreeRepository extends AggregateRepository with AggregateRepositoryAPIs {

  type M[A] = Free[AggregateRepositoryDSL, A]

  type R[_]

  override type SingleResultType[A] = Option[A]

  override def store(aggregate: AggregateType): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate))

  override def resolveBy(id: IdType): Free[AggregateRepositoryDSL, SingleResultType[AggregateType]] =
    Free.liftF[AggregateRepositoryDSL, SingleResultType[AggregateType]](ResolveById(id))

  def interpreter(implicit ec: ExecutionContext): (AggregateRepositoryDSL ~> R)

  def eval[A](program: Free[AggregateRepositoryDSL, A])(implicit ec: ExecutionContext): R[A]

  def run[A](program: Free[AggregateRepositoryDSL, A])(implicit ec: ExecutionContext): R[A]

}
