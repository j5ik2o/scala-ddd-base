package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{AggregateIO, AggregateLongId}
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateIOBaseFeature extends AggregateIO[RIO] {
  override type IdType <: AggregateLongId
  type RecordType <: SkinnyDaoSupport#Record
  type DaoType <: SkinnyDaoSupport#Dao[RecordType]

  protected val dao: DaoType
}

object AggregateIOBaseFeature {
  type RIO[A] = ReaderT[Task, DBSession, A]
}
