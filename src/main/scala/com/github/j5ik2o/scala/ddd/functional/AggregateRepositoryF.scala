package com.github.j5ik2o.scala.ddd.functional

trait AggregateRepositoryDSL[+A]

object AggregateRepositoryF {
  case class ResolveById[ID <: AggregateId, E <: Aggregate](id: ID) extends AggregateRepositoryDSL[Option[E]]
  case class Store[E <: Aggregate](account: E)                      extends AggregateRepositoryDSL[Unit]
  case class Delete[ID <: AggregateId](id: ID)                      extends AggregateRepositoryDSL[Unit]
}
