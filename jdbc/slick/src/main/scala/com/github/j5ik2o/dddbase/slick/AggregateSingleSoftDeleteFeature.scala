package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import monix.eval.Task
import slick.lifted.{ Rep, TableQuery }

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[Task] with AggregateBaseReadFeature {

  override type RecordType <: SlickDaoSupport#SoftDeletableRecord
  override type TableType <: SlickDaoSupport#TableBase[RecordType] with SlickDaoSupport#SoftDeletableTableSupport[
    RecordType
  ]
  protected final val DELETE = "deleted"
  override protected val dao: TableQuery[TableType]

  override def softDelete(id: IdType): Task[Long] =
    Task.deferFutureAction { implicit ec =>
      import profile.api._
      db.run(dao.filter(byCondition(id)).map(_.status).update(DELETE)).map(_.toLong)
    }.asyncBoundary

  abstract override protected def byCondition(id: IdType): TableType => Rep[Boolean] = { e =>
    import profile.api._
    super.byCondition(id)(e) && e.status =!= DELETE
  }

  abstract override protected def byConditions(ids: Seq[IdType]): TableType => Rep[Boolean] = { e =>
    import profile.api._
    super.byConditions(ids)(e) && e.status =!= DELETE
  }

}
