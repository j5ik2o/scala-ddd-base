package com.github.j5ik2o.dddbase.memory

trait MemoryDaoSupport {

  trait Record {
    val id: String
  }

  trait SoftDeletableRecord extends Record {
    type This <: SoftDeletableRecord
    val status: String
    def withStatus(value: String): This
  }

  trait Dao[M[_], R <: Record] {

    def set(record: R): M[Long]

    def setMulti(records: Seq[R]): M[Long]

    def get(id: String): M[Option[R]]

    def getAll: M[Seq[R]]

    def getMulti(ids: Seq[String]): M[Seq[R]]

    def delete(id: String): M[Long]

    def deleteMulti(ids: Seq[String]): M[Long]

  }

  trait DaoSoftDeletable[M[_], R <: SoftDeletableRecord] { this: Dao[M, R] =>

    def softDelete(id: String): M[Long]

    def softDeleteMulti(ids: Seq[String]): M[Long]

  }

}
