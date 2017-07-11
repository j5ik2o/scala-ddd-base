package com.github.j5ik2o.scala.ddd.functional.slick

import cats.data.Kleisli

import scala.concurrent.ExecutionContext

trait SlickDBIOStorageDriver extends SlickStorageDriver {
  import profile.api._

  override type DSL[_] = Kleisli[DBIO, ExecutionContext, _]

  override def store(aggregate: AggregateType): DSL[Unit] = Kleisli { implicit ec =>
    val record = convertToRecord(aggregate)
    val action = (for {
      n <- dao.filter(_.id === aggregate.id.value).update(record)
      _ <- if (n == 0) dao.forceInsert(record) else DBIO.successful(n)
    } yield ()).transactionally
    action.asInstanceOf[DBIO[Unit]]
  }

  override def resolveBy(id: AggregateIdType): DSL[Option[AggregateType]] = Kleisli { implicit ec =>
    val action =
      dao
        .filter(_.id === id.value)
        .result
        .headOption
        .map(convertToAggregate)
    action.asInstanceOf[DBIO[Option[AggregateType]]]
  }

  override def deleteById(id: AggregateIdType): DSL[Unit] = Kleisli { implicit ec =>
    val action = dao.filter(_.id === id.value).delete
    action
      .flatMap { v =>
        if (v == 1)
          DBIO.successful(())
        else
          DBIO.failed(new Exception())
      }
      .asInstanceOf[DBIO[Unit]]
  }

}
