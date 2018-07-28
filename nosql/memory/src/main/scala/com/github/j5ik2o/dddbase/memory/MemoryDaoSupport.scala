package com.github.j5ik2o.dddbase.memory
import monix.eval.Task

trait MemoryDaoSupport {

  trait Record {
    val id: String
  }

  trait SoftDeletableRecord extends Record {
    type This <: SoftDeletableRecord
    val status: String
    def withStatus(value: String): This
  }

  trait Dao[R <: Record] {

    def set(record: R): Task[Long]

    def setMulti(records: Seq[R]): Task[Long]

    def get(id: String): Task[Option[R]]

    def getMulti(ids: Seq[String]): Task[Seq[R]]

    def delete(id: String): Task[Long]

    def deleteMulti(ids: Seq[String]): Task[Long]

  }

  trait DaoSoftDeletable[R <: SoftDeletableRecord] { this: Dao[R] =>

    def softDelete(id: String): Task[Long]

    def softDeleteMulti(ids: Seq[String]): Task[Long]

  }

}
