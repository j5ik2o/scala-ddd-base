package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.{ AggregateRepositoryDSL, AggregateWriter }

trait FreeIOWriteFeature extends FreeIOBaseFeature with AggregateWriter {

  import AggregateRepositoryDSL._

  override def store(aggregate: AggregateType): DSL[Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate))

}
