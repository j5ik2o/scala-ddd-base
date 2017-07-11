package com.github.j5ik2o.scala.ddd.functional.cats

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.{ AggregateIO, AggregateRepositoryDSL }

trait FreeIOBaseFeature extends AggregateIO {
  type DSL[A] = Free[AggregateRepositoryDSL, A]
}
