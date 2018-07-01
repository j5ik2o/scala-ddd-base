package com.github.j5ik2o.dddbase

case class AggregateNotFoundException(id: AggregateId) extends Exception(s"Aggregate is not found: $id")
