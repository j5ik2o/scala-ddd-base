package com.github.j5ik2o.scala.ddd.functional.skinnyorm

import com.github.j5ik2o.scala.ddd.functional.cats.FutureStorageDriver
import scalikejdbc.DB
import skinny.orm.SkinnyCRUDMapperWithId

import scala.concurrent.Future

trait SkinnyORMStorageDriver extends FutureStorageDriver {
  type RecordType
  override type IdValueType = Long

  val dao: SkinnyCRUDMapperWithId[IdValueType, RecordType]

  override type IOContextType = SkinnyORMFutureIOContext

  override type SingleResultType[A] = Option[A]

  protected def convertToRecord(aggregate: AggregateType): RecordType

  protected def convertToAggregate(record: SingleResultType[RecordType]): SingleResultType[AggregateType]

  protected def toNamedValues(record: RecordType): Seq[(Symbol, Any)]

  override def store(aggregate: AggregateType)(implicit ctx: IOContextType): Future[Unit] = {
    implicit val ec = ctx.ec
    Future {
      DB.localTx { dbSession =>
        val namedValues = toNamedValues(convertToRecord(aggregate))
        val result      = dao.updateById(aggregate.id.value).withAttributes(namedValues: _*)(dbSession)
        if (result > 0) ()
        else {
          dao.createWithAttributes(('id -> aggregate.id.value) +: namedValues: _*)(dbSession)
          ()
        }
      }
    }
  }

  override def resolveBy(id: AggregateIdType)(implicit ctx: IOContextType): Future[Option[AggregateType]] = {
    implicit val ec = ctx.ec
    Future {
      convertToAggregate(dao.findById(id.value))
    }
  }

  override def deleteById(id: AggregateIdType)(implicit ctx: IOContextType): Future[Unit] = {
    implicit val ec = ctx.ec
    Future {
      dao.deleteById(id.value)
      ()
    }
  }

}
