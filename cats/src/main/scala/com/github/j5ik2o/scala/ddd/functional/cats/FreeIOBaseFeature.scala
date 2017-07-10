package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import cats.{ ~>, Monad }
import com.github.j5ik2o.scala.ddd.functional._

import scala.language.higherKinds

trait FreeIOBaseFeature extends AggregateIO { self =>

  type DSL[A] = Free[AggregateRepositoryDSL, A]
  type EvalType[_]
  type DriverType <: StorageDriver {
    type AggregateIdType = self.AggregateIdType
    type AggregateType   = self.AggregateType
    type IOContextType   = self.IOContextType
  }

  def driver: DriverType

  protected def interpreter(implicit ctx: IOContextType): (AggregateRepositoryDSL ~> EvalType) =
    new (AggregateRepositoryDSL ~> EvalType) {
      override def apply[A](fa: AggregateRepositoryDSL[A]): EvalType[A] = fa match {
        case Store(aggregate) =>
          driver
            .store(aggregate.asInstanceOf[AggregateType])(ctx.asInstanceOf[IOContextType])
            .asInstanceOf[EvalType[A]]
        case ResolveById(id) =>
          driver
            .resolveBy(id.asInstanceOf[AggregateIdType])(ctx.asInstanceOf[IOContextType])
            .asInstanceOf[EvalType[A]]
        case Delete(id) =>
          driver
            .deleteById(id.asInstanceOf[AggregateIdType])(ctx.asInstanceOf[IOContextType])
            .asInstanceOf[EvalType[A]]
      }
    }

  def run[A](program: Free[AggregateRepositoryDSL, A])(implicit ctx: IOContextType, M: Monad[EvalType]): EvalType[A] = {
    program.foldMap(interpreter)
  }

}
