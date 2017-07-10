package com.github.j5ik2o.scala.ddd.functional.slick

import scala.concurrent.ExecutionContext

trait SlickDBIODriver extends SlickDriver {
  import profile.api._
  override type DSL[_] = DBIO[_]

  override def store(aggregate: AggregateType)(implicit ctx: ExecutionContext): DSL[Unit] = {
    val record = convertToRecord(aggregate)
    val action = (for {
      n <- dao.filter(_.id === aggregate.id.value).update(record)
      _ <- if (n == 0) dao.forceInsert(record) else DBIO.successful(n)
    } yield ()).transactionally
    action.asInstanceOf[DSL[Unit]]
  }

  override def resolveBy(id: AggregateIdType)(implicit ctx: ExecutionContext): DSL[Option[AggregateType]] = {
    val action =
      dao
        .filter(_.id === id.value)
        .result
        .headOption
        .map(e => convertToAggregate(e.asInstanceOf[SingleResultType[RecordType]]))
    action.asInstanceOf[DSL[Option[AggregateType]]]
  }

  override def deleteById(id: AggregateIdType)(implicit ec: ExecutionContext): DSL[Unit] = {
    val action = dao.filter(_.id === id.value).delete
    action
      .flatMap { v =>
        if (v == 1)
          DBIO.successful(())
        else
          DBIO.failed(new Exception())
      }
      .asInstanceOf[DSL[Unit]]
  }

}
