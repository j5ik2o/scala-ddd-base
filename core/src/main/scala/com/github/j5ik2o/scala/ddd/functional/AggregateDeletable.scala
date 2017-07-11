package com.github.j5ik2o.scala.ddd.functional

trait AggregateDeletable { this: AggregateWriter =>

  def deleteById(id: AggregateIdType): DSL[Unit]

}
