package com.github.j5ik2o.scala.ddd.functional.driver

trait AggregateWriter extends AggregateIO {

  def store(aggregate: AggregateType): DSL[Unit]

}
