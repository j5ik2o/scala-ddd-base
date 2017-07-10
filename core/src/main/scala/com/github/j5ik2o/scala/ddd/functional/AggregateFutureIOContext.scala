package com.github.j5ik2o.scala.ddd.functional

import scala.concurrent.ExecutionContext

trait AggregateFutureIOContext extends AggregateIOContext {
  val ec: ExecutionContext
}
