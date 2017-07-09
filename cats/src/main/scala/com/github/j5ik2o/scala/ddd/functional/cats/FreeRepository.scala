package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import cats.{ ~>, Monad }
import com.github.j5ik2o.scala.ddd.functional._

trait FreeIOBaseFeature extends AggregateIO { self =>

  type DSL[A] = Free[AggregateRepositoryDSL, A]
  type EvalType[_]
  //type RealizeType[_]
  type DriverType <: Driver {
    type AggregateIdType = self.AggregateIdType
    type AggregateType   = self.AggregateType
    type IOContextType   = self.IOContextType
    // type EvalType[_]     = self.EvalType[_]
    //type RealizeType[_]  = self.RealizeType[_]
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

trait FreeIOWriteFeature extends FreeIOBaseFeature with AggregateWriter {

  override def store(aggregate: AggregateType)(implicit ctx: IOContextType): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate))

}

trait FreeIODeleteFeature extends FreeIOBaseFeature with FreeIOWriteFeature with AggregateDeletable {
  override def deleteById(id: AggregateIdType)(implicit ctx: IOContextType): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Delete(id))
}

trait FreeIOReadFeature extends FreeIOBaseFeature with AggregateReader {

  override type SingleResultType[A] = Option[A]

  override def resolveBy(
      id: AggregateIdType
  )(implicit ctx: IOContextType): Free[AggregateRepositoryDSL, SingleResultType[AggregateType]] =
    Free.liftF[AggregateRepositoryDSL, SingleResultType[AggregateType]](ResolveById(id))

}

trait FreeIORepositoryFeature extends FreeIOReadFeature with FreeIOWriteFeature
