package com.github.j5ik2o.dddbase.example.repository.util

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }

trait JdbcSpecSupport extends ScalaFutures with ScalaFuturesSupportSpec {
  this: FlywayWithMySQLSpecSupport =>
  val tables: Seq[String]

  def jdbcPort: Int = mySQLdConfig.port.get
}
