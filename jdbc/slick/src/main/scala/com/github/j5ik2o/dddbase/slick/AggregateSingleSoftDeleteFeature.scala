package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import monix.eval.Task
import slick.lifted.{ Rep, TableQuery }

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[RIO] with AggregateBaseReadFeature {

  override type RecordType <: SlickDaoSupport#SoftDeletableRecord
  override type TableType <: SlickDaoSupport#TableBase[RecordType] with SlickDaoSupport#SoftDeletableTableSupport[
    RecordType
  ]
  protected final val DELETE = "deleted"
  override protected val dao: TableQuery[TableType]

  override def softDelete(id: IdType): RIO[Long] =
    Task.deferFutureAction { implicit ec =>
      import profile.api._
      db.run(dao.filter(_.id === id.value).map(_.status).update(DELETE)).map(_.toLong)
    }

  override protected def byCondition(
      id: IdType
  ): TableType => Rep[Boolean] = { e =>
    import profile.api._
    e.id === id.value && e.status =!= DELETE
  }

  override protected def byConditions(
      ids: Seq[IdType]
  ): TableType => Rep[Boolean] = { e =>
    import profile.api._
    e.id.inSet(ids.map(_.value)) && e.status =!= DELETE
  }

}
