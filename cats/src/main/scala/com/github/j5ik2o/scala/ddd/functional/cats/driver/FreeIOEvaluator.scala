package com.github.j5ik2o.scala.ddd.functional.cats.driver

import cats.free.Free
import cats.{ ~>, Monad }
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL
import com.github.j5ik2o.scala.ddd.functional.cats.StorageDriver
import com.github.j5ik2o.scala.ddd.functional.driver.AggregateIO

trait FreeIOEvaluator extends AggregateIO { self =>
  import AggregateRepositoryDSL._

  type DSL[A] = Free[AggregateRepositoryDSL, A]
  type EvalType[_]
  type DriverType <: StorageDriver {
    type AggregateIdType = self.AggregateIdType
    type AggregateType   = self.AggregateType
  }

  val driver: DriverType

  protected def interpreter: (AggregateRepositoryDSL ~> EvalType) =
    new (AggregateRepositoryDSL ~> EvalType) {
      override def apply[A](fa: AggregateRepositoryDSL[A]): EvalType[A] = fa match {
        case Store(aggregate) =>
          driver
            .store(aggregate.asInstanceOf[AggregateType])
            .asInstanceOf[EvalType[A]]
        case ResolveById(id) =>
          driver
            .resolveBy(id.asInstanceOf[AggregateIdType])
            .asInstanceOf[EvalType[A]]
        case Delete(id) =>
          driver
            .deleteById(id.asInstanceOf[AggregateIdType])
            .asInstanceOf[EvalType[A]]
      }
    }

  def run[A](program: Free[AggregateRepositoryDSL, A])(implicit M: Monad[EvalType]): EvalType[A] = {
    program.foldMap(interpreter)
  }
}
