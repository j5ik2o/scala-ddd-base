package com.github.j5ik2o.dddbase.dynamodb
import com.github.j5ik2o.reactive.dynamodb.monix.DynamoDBTaskClientV2
import monix.eval.Task

trait DynamoDBDaoSupport {

  trait Record[ID] {
    val id: ID
  }

  trait SoftDeletableRecord[ID] extends Record[ID] {
    type This <: SoftDeletableRecord[ID]
    val status: String
    def withStatus(value: String): This
  }

  trait Dao[ID, R <: Record[ID]] {

    protected def client: DynamoDBTaskClientV2

    def put(record: R): Task[Long]

    def putMulti(records: Seq[R]): Task[Long]

    def get(id: ID): Task[Option[R]]

    def getMulti(ids: Seq[ID]): Task[Seq[R]]

    def delete(id: ID): Task[Long]

    def deleteMulti(ids: Seq[ID]): Task[Long]

  }

  trait DaoSoftDeletable[ID, R <: SoftDeletableRecord[ID]] { this: Dao[ID, R] =>

    def softDelete(id: ID): Task[Long]

    def softDeleteMulti(ids: Seq[ID]): Task[Long]

  }

}
