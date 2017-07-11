package com.github.j5ik2o.scala.ddd.functional

trait AggregateReader extends AggregateIO {

  type SingleResultType[_]

  def resolveBy(id: AggregateIdType): DSL[SingleResultType[AggregateType]]

}
