package com.github.j5ik2o.dddbase.skinny

import scalikejdbc._
import skinny.orm.feature.{ NoIdCUDFeature, NoIdFinderFeature }
import skinny.orm.{ SkinnyCRUDMapperWithId, SkinnyNoIdCRUDMapper }

trait SkinnyDaoSupport {

  trait Record[ID] extends Product {
    val id: ID
  }

  trait SoftDeletableRecord[ID] extends Record[ID] {
    val status: String
  }

  trait Dao[ID, R <: Record[ID]] extends NoIdCUDFeature[R] with NoIdFinderFeature[R] {

    protected def toNamedIds(record: R): Seq[(Symbol, Any)]
    protected def toNamedValues(record: R): Seq[(Symbol, Any)]
    protected def byCondition(id: ID): SQLSyntax

    def createOrUpdate(record: R): Long =
      DB localTx { implicit dbSession =>
        if (countBy(byCondition(record.id)) == 1)
          update(record)
        else
          create(record)
      }

    def create(record: R)(implicit session: DBSession): Long = {
      createWithAttributes(toNamedIds(record) ++ toNamedValues(record): _*)
      1L
    }

    def createAll(records: Seq[R])(implicit session: DBSession): Seq[Long] =
      records.map(create)

    def update(record: R)(implicit session: DBSession): Long =
      updateById(record.id).withAttributes(toNamedValues(record): _*).toLong

    def updateAll(records: Seq[R])(implicit session: DBSession): Seq[Long] =
      records.map(update)

    def updateById(id: ID): UpdateOperationBuilder

    def deleteById(id: ID)(implicit s: DBSession = autoSession): Int
  }

  trait DaoWithId[ID, R <: Record[ID]] extends Dao[ID, R] with SkinnyCRUDMapperWithId[ID, R] {

    implicit def pbf: ParameterBinderFactory[ID]

    override def useAutoIncrementPrimaryKey: Boolean = false
    override def useExternalIdGenerator: Boolean     = true

    def rawValueToId(value: Any): ID
    def idToRawValue(id: ID): Any

    protected def toNamedIds(record: R): Seq[(Symbol, Any)] = Seq('id -> idToRawValue(record.id))

    protected def toNamedValues(record: R): Seq[(Symbol, Any)]

    protected def byCondition(id: ID): SQLSyntax =
      sqls.eq(column.id, id)

  }

  trait DaoWithCompositeId[ID, R <: Record[ID]] extends Dao[ID, R] with SkinnyNoIdCRUDMapper[R] {

    override def updateById(id: ID): UpdateOperationBuilder =
      updateBy(byCondition(id))

    override def deleteById(id: ID)(implicit s: DBSession): Int =
      deleteBy(byCondition(id))

  }

}
