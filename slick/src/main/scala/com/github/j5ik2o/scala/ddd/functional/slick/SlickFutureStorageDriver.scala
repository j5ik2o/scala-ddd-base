package com.github.j5ik2o.scala.ddd.functional.slick

import cats.data.Kleisli
import com.github.j5ik2o.scala.ddd.functional.cats.StorageDriver

import scala.concurrent.{ ExecutionContext, Future }

trait SlickFutureStorageDriver extends StorageDriver with SlickStorageDriver {

  import profile.api._

  //  override type IOContextType = SlickFutureIOContext
  override type DSL[A] = Kleisli[Future, ExecutionContext, A]

  override def store(aggregate: AggregateType): DSL[Unit] = Kleisli { implicit ec =>
    val record = convertToRecord(aggregate)
    val action = (for {
      n <- dao.filter(_.id === aggregate.id.value).update(record)
      _ <- if (n == 0) dao.forceInsert(record) else DBIO.successful(n)
    } yield ()).transactionally
    db.run(action)
  }

  override def resolveBy(id: AggregateIdType): DSL[Option[AggregateType]] = Kleisli { implicit ec =>
    val action =
      dao
        .filter(_.id === id.value)
        .result
        .headOption
        .map(e => convertToAggregate(e))
    db.run(action)
  }

  override def deleteById(id: AggregateIdType): DSL[Unit] = Kleisli { implicit ec =>
    val action = dao.filter(_.id === id.value).delete
    db.run(
      action
        .flatMap { v =>
          (if (v == 1)
             DBIO.successful(())
           else
             DBIO.failed(new Exception())): DBIO[Unit]
        }
    )
  }
}
