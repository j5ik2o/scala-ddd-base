package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.AggregateIO
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateIOBaseFeature extends AggregateIO[RIO] {
  type RecordIdType
  type RecordType <: DynamoDBDaoSupport#Record[RecordIdType]
  type DaoType <: DynamoDBDaoSupport#Dao[RecordIdType, RecordType]

  protected val dao: DaoType
  protected def toRecordId(id: IdType): RecordIdType
}

object AggregateIOBaseFeature {
  type RIO[A] = Task[A]
}
