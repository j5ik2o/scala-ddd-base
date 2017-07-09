package com.github.j5ik2o.scala.ddd.functional

trait AggregateRepositoryDSL[+R]
case class Store[A <: Aggregate](aggregate: A)                    extends AggregateRepositoryDSL[Unit]
case class ResolveById[ID <: AggregateId, A <: Aggregate](id: ID) extends AggregateRepositoryDSL[Option[A]]
case class Delete[ID <: AggregateId](id: ID)                      extends AggregateRepositoryDSL[Unit]
