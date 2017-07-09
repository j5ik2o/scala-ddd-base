package com.github.j5ik2o.scala.ddd.functional

trait AggregateRepositoryAPIs { this: AggregateIO =>

  trait AggregateRepositoryDSL[+A]
  case class ResolveById(id: AggregateIdType)(implicit val ctx: IOContext)
      extends AggregateRepositoryDSL[Option[AggregateType]]
  case class Store(aggregate: AggregateType)(implicit val ctx: IOContext) extends AggregateRepositoryDSL[Unit]
  case class Delete(id: AggregateIdType)(implicit val ctx: IOContext)     extends AggregateRepositoryDSL[Unit]

}
