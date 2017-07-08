package com.github.j5ik2o.scala.ddd.functional

trait AggregateWriter extends AggregateIO {

  def store(aggregate: AggregateType): M[Unit]

}
