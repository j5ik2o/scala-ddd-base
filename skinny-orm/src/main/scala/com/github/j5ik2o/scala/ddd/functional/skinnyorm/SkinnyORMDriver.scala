package com.github.j5ik2o.scala.ddd.functional.skinnyorm

import com.github.j5ik2o.scala.ddd.functional.cats.Driver
import scalikejdbc.{ DB, DBSession }
import skinny.orm.SkinnyCRUDMapperWithId

trait SkinnyORMDriver extends Driver {
  type RecordType
  override type IdValueType = Long

  val dao: SkinnyCRUDMapperWithId[IdValueType, RecordType]

  override type DSL[A]        = Either[Exception, A]
  override type IOContextType = DBSession

  override type SingleResultType[A] = Option[A]

  protected def convertToRecord(aggregate: AggregateType): RecordType

  protected def convertToAggregate(record: SingleResultType[RecordType]): SingleResultType[AggregateType]

  protected def toNamedValues(record: RecordType): Seq[(Symbol, Any)]

  override def store(aggregate: AggregateType)(implicit ctx: IOContextType): Either[Exception, Unit] = {
    try {
      DB.localTx { dbSession =>
        val namedValues = toNamedValues(convertToRecord(aggregate))
        val result      = dao.updateById(aggregate.id.value).withAttributes(namedValues: _*)(dbSession)
        if (result > 0) Right(())
        else {
          dao.createWithAttributes(('id -> aggregate.id.value) +: namedValues: _*)(dbSession)
          Right(())
        }
      }
    } catch {
      case ex: Exception =>
        Left(ex)
    }
  }

  override def resolveBy(id: AggregateIdType)(implicit ctx: DBSession): Either[Exception, Option[AggregateType]] = {
    try {
      Right(convertToAggregate(dao.findById(id.value)))
    } catch {
      case ex: Exception =>
        Left(ex)
    }
  }

  override def deleteById(id: AggregateIdType)(implicit ec: DBSession): Either[Exception, Unit] = {
    try {
      dao.deleteById(id.value)
      Right(())
    } catch {
      case ex: Exception =>
        Left(ex)
    }
  }

}
