package com.github.j5ik2o.scala.ddd.functional.cats.driver

import cats.free.Free
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL
import com.github.j5ik2o.scala.ddd.functional.driver.AggregateIO

trait FreeIOBaseFeature extends AggregateIO {
  type DSL[A] = Free[AggregateRepositoryDSL, A]
}
