package com.github.j5ik2o.scala.ddd.functional.slick

import com.github.j5ik2o.scala.ddd.functional.AggregateFutureIOContext

import scala.concurrent.ExecutionContext

case class SlickFutureIOContext(ec: ExecutionContext) extends AggregateFutureIOContext
