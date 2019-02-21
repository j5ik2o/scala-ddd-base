package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.AggregateIO
import monix.eval.Task

trait AggregateIOBaseFeature extends AggregateIO[Task] {
  type RecordIdType
  type RecordType <: DynamoDBDaoSupport#Record[RecordIdType]
  type DaoType <: DynamoDBDaoSupport#Dao[Task, RecordIdType, RecordType]

  protected val dao: DaoType
  protected def toRecordId(id: IdType): RecordIdType
}
