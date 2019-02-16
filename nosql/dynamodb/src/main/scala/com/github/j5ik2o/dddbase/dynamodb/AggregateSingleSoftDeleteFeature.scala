package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.AggregateSingleSoftDeletable
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO

trait AggregateSingleSoftDeleteFeature extends AggregateSingleSoftDeletable[RIO] with AggregateBaseReadFeature {
  override type RecordType <: DynamoDBDaoSupport#SoftDeletableRecord
  override type DaoType <: DynamoDBDaoSupport#Dao[RecordType] with DynamoDBDaoSupport#DaoSoftDeletable[RecordType]

  override def softDelete(id: IdType): RIO[Long] = dao.softDelete(id.value.toString)

}
