package com.github.j5ik2o.scala.ddd.functional.slick

import com.github.j5ik2o.scala.ddd.functional.cats.Driver
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait SlickDriver extends Driver {
  val profile: JdbcProfile
  val db: JdbcProfile#Backend#Database

  import profile.api._
  override type IdValueType = Long
  type TableType <: Table[RecordType] {
    def id: Rep[AggregateType#IdType#IdValueType]
    type TableElementType = RecordType
  }
  override type SingleResultType[A] = Option[A]
  override type IOContextType       = ExecutionContext
  protected val dao: TableQuery[TableType]
}
