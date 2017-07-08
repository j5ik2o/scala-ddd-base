package com.github.j5ik2o.scala.ddd.functional.slick

import com.github.j5ik2o.scala.ddd.functional.{ Aggregate, DaoRecord }
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait Dao extends com.github.j5ik2o.scala.ddd.functional.Dao {
  val profile: JdbcProfile
  val db: JdbcProfile#Backend#Database

  import profile.api._

  type RecordType <: DaoRecord
  type AggregateType <: Aggregate
  type AggregateIdType <: AggregateType#IdType

  type TableType <: Table[RecordType] {
    def id: Rep[IdValueType]
    type TableElementType = RecordType
  }

  type DBIOA[A] = DBIO[A]

  protected val dao: TableQuery[TableType]

  protected def convertToRecord(aggregate: AggregateType): RecordType

  protected def convertToAggregate(record: RecordType): AggregateType

  protected def findById[A](id: AggregateIdType)(implicit ec: ExecutionContext): DBIOA[A] = {
    val action =
      dao.filter(_.id === id.value).result.headOption.map(convertToAggregate)
    action.asInstanceOf[DBIOA[A]]
  }

  protected def store[A](aggregate: AggregateType)(implicit ec: ExecutionContext): DBIOA[A] = {
    val record = convertToRecord(aggregate)
    val action = (for {
      n <- dao.filter(_.id === aggregate.id.value).update(record)
      _ <- if (n == 0) dao.forceInsert(record) else DBIO.successful(n)
    } yield ()).transactionally
    action.asInstanceOf[DBIOA[A]]
  }

  protected def deleteById[A](id: AggregateIdType)(implicit ec: ExecutionContext): DBIOA[A] = {
    val action = dao.filter(_.id === id.value).delete
    action
      .flatMap { v =>
        if (v == 1)
          DBIO.successful(())
        else
          DBIO.failed(new Exception())
      }
      .asInstanceOf[DBIOA[A]]
  }

}
