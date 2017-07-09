package com.github.j5ik2o.scala.ddd.functional.slick

import com.github.j5ik2o.scala.ddd.functional.cats.Driver
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait Slick3Driver extends Driver with CatsDBIOMonadInstance {
  val profile: JdbcProfile
  val db: JdbcProfile#Backend#Database

  import profile.api._

  type RecordType
  override type IdValueType = Long
  type TableType <: Table[RecordType] {
    def id: Rep[AggregateType#IdType#IdValueType]
    type TableElementType = RecordType
  }

  override type DSL[_]    = DBIO[_]
  override type IOContext = ExecutionContext
  protected val dao: TableQuery[TableType]

  protected def convertToRecord(aggregate: AggregateType): RecordType

  protected def convertToAggregate(record: SingleResultType[RecordType]): SingleResultType[AggregateType]

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
