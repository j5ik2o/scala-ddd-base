package com.github.j5ik2o.scala.ddd.functional.driver

trait AggregateReader extends AggregateIO {

  def resolveBy(id: AggregateIdType): DSL[Option[AggregateType]]

}
