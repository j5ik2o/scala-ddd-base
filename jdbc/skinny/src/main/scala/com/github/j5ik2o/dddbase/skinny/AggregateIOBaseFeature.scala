package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateIO, AggregateId }
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateIOBaseFeature extends AggregateIO[RIO] {
  override type IdType <: AggregateId
  type RecordIdType
  type RecordType <: SkinnyDaoSupport#Record[RecordIdType]
  type DaoType <: SkinnyDaoSupport#Dao[RecordIdType, RecordType]

  protected val dao: DaoType
  protected def toRecordId(id: IdType): RecordIdType
}

object AggregateIOBaseFeature {
  type RIO[A] = ReaderT[Task, DBSession, A]
}
