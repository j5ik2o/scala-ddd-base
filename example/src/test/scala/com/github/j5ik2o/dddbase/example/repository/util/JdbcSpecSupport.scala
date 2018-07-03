package com.github.j5ik2o.dddbase.example.repository.util

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

trait JdbcSpecSupport extends ScalaFutures {
  this: FlywayWithMySQLSpecSupport =>
  val tables: Seq[String]

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(30, Seconds)), interval = scaled(Span(15, Millis)))

  def jdbcPort: Int = mySQLdConfig.port.get
}
