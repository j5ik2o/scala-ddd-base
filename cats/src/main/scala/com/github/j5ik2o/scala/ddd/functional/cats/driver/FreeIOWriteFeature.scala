package com.github.j5ik2o.scala.ddd.functional.cats.driver

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL
import com.github.j5ik2o.scala.ddd.functional.driver.AggregateWriter

trait FreeIOWriteFeature extends FreeIOBaseFeature with AggregateWriter {

  import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL._

  override def store(aggregate: AggregateType): DSL[Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate))

}
