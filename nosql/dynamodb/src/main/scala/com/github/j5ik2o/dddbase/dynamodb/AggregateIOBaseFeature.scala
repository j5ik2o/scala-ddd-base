package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.dddbase.AggregateIO
import com.github.j5ik2o.dddbase.dynamodb.AggregateIOBaseFeature.RIO
import monix.eval.Task

trait AggregateIOBaseFeature extends AggregateIO[RIO] {
  type RecordType <: DynamoDBDaoSupport#Record
  type DaoType <: DynamoDBDaoSupport#Dao[RecordType]

  protected val dao: DaoType

}

object AggregateIOBaseFeature {
  type RIO[A] = Task[A]
}
