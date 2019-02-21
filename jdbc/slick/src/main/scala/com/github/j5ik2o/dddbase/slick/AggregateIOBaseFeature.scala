package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateIO
import monix.eval.Task
import slick.jdbc.JdbcProfile
import slick.lifted.{ Rep, TableQuery }

trait AggregateIOBaseFeature extends AggregateIO[Task] {
  type RecordType <: SlickDaoSupport#Record
  type TableType <: SlickDaoSupport#TableBase[RecordType]

  protected val profile: JdbcProfile

  protected val db: JdbcProfile#Backend#Database

  protected val dao: TableQuery[TableType]

  protected def byCondition(id: IdType): TableType => Rep[Boolean]

  protected def byConditions(ids: Seq[IdType]): TableType => Rep[Boolean]

}
