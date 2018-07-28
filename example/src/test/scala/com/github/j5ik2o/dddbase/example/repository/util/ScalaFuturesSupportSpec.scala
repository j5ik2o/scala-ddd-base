package com.github.j5ik2o.dddbase.example.repository.util
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }

trait ScalaFuturesSupportSpec { this: ScalaFutures =>
  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(60, Seconds)), interval = scaled(Span(1, Seconds)))
}
