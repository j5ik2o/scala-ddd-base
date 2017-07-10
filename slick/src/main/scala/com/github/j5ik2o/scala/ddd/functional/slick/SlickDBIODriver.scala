package com.github.j5ik2o.scala.ddd.functional.slick

trait SlickDBIODriver extends SlickDriver {
  import profile.api._
  override type DSL[_]        = DBIO[_]
  override type IOContextType = SlickFutureIOContext

  override def store(aggregate: AggregateType)(implicit ctx: IOContextType): DSL[Unit] = {
    implicit val ec = ctx.ec
    val record      = convertToRecord(aggregate)
    val action = (for {
      n <- dao.filter(_.id === aggregate.id.value).update(record)
      _ <- if (n == 0) dao.forceInsert(record) else DBIO.successful(n)
    } yield ()).transactionally
    action.asInstanceOf[DSL[Unit]]
  }

  override def resolveBy(id: AggregateIdType)(implicit ctx: IOContextType): DSL[Option[AggregateType]] = {
    implicit val ec = ctx.ec
    val action =
      dao
        .filter(_.id === id.value)
        .result
        .headOption
        .map(convertToAggregate)
    action.asInstanceOf[DSL[Option[AggregateType]]]
  }

  override def deleteById(id: AggregateIdType)(implicit ctx: IOContextType): DSL[Unit] = {
    implicit val ec = ctx.ec
    val action      = dao.filter(_.id === id.value).delete
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
