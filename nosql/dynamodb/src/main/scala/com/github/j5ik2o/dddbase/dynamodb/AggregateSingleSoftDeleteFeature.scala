package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import monix.eval.Task

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[Task] with AggregateBaseReadFeature {
  override type RecordType <: DynamoDBDaoSupport#SoftDeletableRecord[RecordIdType]
  override type DaoType <: DynamoDBDaoSupport#Dao[Task, RecordIdType, RecordType] with DynamoDBDaoSupport#DaoSoftDeletable[
    Task,
    RecordIdType,
    RecordType
  ]

  override def softDelete(id: IdType): Task[Long] = dao.softDelete(toRecordId(id))

}
