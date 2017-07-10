package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.{ AggregateRepositoryDSL, AggregateWriter, Store }

trait FreeIOWriteFeature extends FreeIOBaseFeature with AggregateWriter {

  override def store(aggregate: AggregateType)(implicit ctx: IOContextType): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate))

}
