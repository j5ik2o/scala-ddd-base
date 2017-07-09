package com.github.j5ik2o.scala.ddd.functional.example.domain

import cats.free.Free
import cats.~>
import com.github.j5ik2o.scala.ddd.functional.cats.{ FreeIODeleteFeature, FreeIORepositoryFeature }
import com.github.j5ik2o.scala.ddd.functional.example.slick3.UserSlick3Driver

import scala.concurrent.{ ExecutionContext, Future }

class UserRepository(val driver: UserSlick3Driver) extends FreeIORepositoryFeature with FreeIODeleteFeature {
  override type IdValueType     = Long
  override type AggregateIdType = UserId
  override type AggregateType   = User
  override type EvalType[A]     = driver.DSL[A]
  override type RealizeType[A]  = Future[A]
  override type IOContextType   = ExecutionContext

  override protected lazy val interpreter: AggregateRepositoryDSL ~> EvalType =
    new (AggregateRepositoryDSL ~> EvalType) {
      override def apply[A](fa: AggregateRepositoryDSL[A]): EvalType[A] = fa match {
        case s @ Store(aggregate) =>
          driver.store(aggregate)(s.ctx)
        case r @ ResolveById(id) =>
          driver.resolveBy(id)(r.ctx)
        case d @ Delete(id) =>
          driver.deleteById(id)(d.ctx)
      }
    }

  override def eval[A](program: Free[AggregateRepositoryDSL, A])(implicit ctx: IOContextType): RealizeType[A] = ???

  override def realize[A](program: Free[AggregateRepositoryDSL, A])(implicit ctx: IOContextType): RealizeType[A] =
    ???
}
