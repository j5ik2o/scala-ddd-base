package com.github.j5ik2o.dddbase.skinny

import scalikejdbc._
import skinny.orm._

trait SkinnyDaoSupport {

  trait Record extends Product {
    val id: Long
  }

  trait SoftDeletableRecord extends Record {
    val status: String
  }

  trait Dao[R <: Record] extends SkinnyCRUDMapper[R] {

    protected def toNamedValues(record: R): Seq[(Symbol, Any)]

    def createOrUpdate(record: R): Long =
      DB localTx { implicit dbSession =>
        if (countBy(sqls.eq(defaultAlias.id, record.id)) == 1)
          update(record)
        else
          create(record)
      }

    def create(record: R)(implicit session: DBSession): Long =
      createWithAttributes(('id -> record.id) +: toNamedValues(record): _*)

    def createAll(records: Seq[R])(implicit session: DBSession): Seq[Long] =
      records.map(create)

    def update(record: R)(implicit session: DBSession): Long =
      updateById(record.id).withAttributes(toNamedValues(record): _*).toLong

    def updateAll(records: Seq[R])(implicit session: DBSession): Seq[Long] =
      records.map(update)

  }

}
