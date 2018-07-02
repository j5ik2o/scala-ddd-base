package com.github.j5ik2o.dddbase.skinny

import scalikejdbc._
import skinny.orm._

trait SkinnyDaoSupport {

  trait Record {
    val id: Long
  }

  trait SoftDeletableRecord extends Record {
    val status: String
  }

  trait Dao[R <: Record] extends SkinnyCRUDMapper[R] {
//    override def extract(rs: WrappedResultSet, n: ResultName[R]): R =
//      autoConstruct(rs, n)

    protected def toNamedValues(record: R): Seq[(Symbol, Any)]
    def create(record: R)(implicit session: DBSession): Long =
      createWithAttributes(toNamedValues(record): _*)
    def update(record: R)(implicit session: DBSession): Int =
      updateById(record.id).withAttributes(toNamedValues(record): _*)

  }

}
