package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[RIO] with AggregateBaseReadFeature {
  override type RecordType <: DynamoDBDaoSupport#SoftDeletableRecord[RecordIdType]
  override type DaoType <: DynamoDBDaoSupport#Dao[RecordIdType, RecordType] with DynamoDBDaoSupport#DaoSoftDeletable[
    RecordIdType,
    RecordType
  ]

  override def softDelete(id: IdType): RIO[Long] = dao.softDelete(toRecordId(id))

}
