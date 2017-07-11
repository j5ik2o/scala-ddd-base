package com.github.j5ik2o.scala.ddd.functional.slick

import com.github.j5ik2o.scala.ddd.functional.cats.StorageDriver
import slick.jdbc.JdbcProfile

trait SlickStorageDriver extends StorageDriver {
  val profile: JdbcProfile
  val db: JdbcProfile#Backend#Database

  import profile.api._
  override type IdValueType = Long
  type TableType <: Table[RecordType] {
    def id: Rep[AggregateType#IdType#IdValueType]
    type TableElementType = RecordType
  }

  protected val dao: TableQuery[TableType]
}
