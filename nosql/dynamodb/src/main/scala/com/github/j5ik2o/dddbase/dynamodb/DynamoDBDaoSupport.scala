package com.github.j5ik2o.dddbase.dynamodb

import com.github.j5ik2o.reactive.aws.dynamodb.monix.DynamoDbMonixClient

trait DynamoDBDaoSupport {

  trait Record[ID] {
    val id: ID
  }

  trait SoftDeletableRecord[ID] extends Record[ID] {
    type This <: SoftDeletableRecord[ID]
    val status: String
    def withStatus(value: String): This
  }

  trait Dao[M[_], ID, R <: Record[ID]] {

    protected def client: DynamoDbMonixClient

    def put(record: R): M[Long]

    def putMulti(records: Seq[R]): M[Long]

    def get(id: ID): M[Option[R]]

    def getMulti(ids: Seq[ID]): M[Seq[R]]

    def delete(id: ID): M[Long]

    def deleteMulti(ids: Seq[ID]): M[Long]

  }

  trait DaoSoftDeletable[M[_], ID, R <: SoftDeletableRecord[ID]] { this: Dao[M, ID, R] =>

    def softDelete(id: ID): M[Long]

    def softDeleteMulti(ids: Seq[ID]): M[Long]

  }

}
