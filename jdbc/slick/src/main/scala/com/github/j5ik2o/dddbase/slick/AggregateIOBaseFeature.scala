package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature._
import com.github.j5ik2o.dddbase.{AggregateIO, AggregateLongId}
import monix.eval.Task
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

trait AggregateIOBaseFeature extends AggregateIO[RIO] {
  override type IdType <: AggregateLongId
  type RecordType <: SlickDaoSupport#Record
  type TableType <: SlickDaoSupport#TableBase[RecordType]

  protected val profile: JdbcProfile

  protected val db: JdbcProfile#Backend#Database

  protected val dao: TableQuery[TableType]
}

object AggregateIOBaseFeature {
  type RIO[A] = Task[A]
}
