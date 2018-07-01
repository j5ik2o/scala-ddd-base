package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateSoftDeletable
import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateSoftDeleteFeature extends AggregateSoftDeletable[RIO] with AggregateBaseReadFeature {

  import profile.api._

  override type RecordType <: SlickDaoSupport#SoftDeletableRecord
  override type TableType <: SlickDaoSupport#TableBase[RecordType] with SlickDaoSupport#SoftDeletableTableSupport[
    RecordType
  ]
  protected final val DELETE = "deleted"
  override protected val dao: TableQuery[TableType]

  override def softDelete(id: IdType): Task[Int] = Task.deferFutureAction { implicit ec =>
    db.run(dao.filter(_.id === id.value).map(_.status).update(DELETE))
  }

  override protected def byCondition(
      id: IdType
  ): TableType => Rep[Boolean] = { e =>
    e.id === id.value && e.status =!= DELETE
  }

  override protected def byConditions(
      ids: Seq[IdType]
  ): TableType => Rep[Boolean] = { e =>
    e.id.inSet(ids.map(_.value)) && e.status =!= DELETE
  }

}
