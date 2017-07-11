package com.github.j5ik2o.scala.ddd.functional.skinnyorm

import cats.data.Kleisli
import com.github.j5ik2o.scala.ddd.functional.cats.StorageDriver
import scalikejdbc.DB
import skinny.orm.SkinnyCRUDMapperWithId

import scala.concurrent.Future

trait SkinnyORMStorageDriver extends StorageDriver {
  type RecordType
  override type IdValueType = Long
  override type DSL[A]      = Kleisli[Future, SkinnyORMFutureIOContext, A]

  val dao: SkinnyCRUDMapperWithId[IdValueType, RecordType]

  private def withContext[A](body: SkinnyORMFutureIOContext => Future[A]): DSL[A] =
    Kleisli[Future, SkinnyORMFutureIOContext, A](body)

  protected def convertToRecord(aggregate: AggregateType): RecordType

  protected def convertToAggregate(record: Option[RecordType]): Option[AggregateType]

  protected def toNamedValues(record: RecordType): Seq[(Symbol, Any)]

  override def store(aggregate: AggregateType): DSL[Unit] = withContext[Unit] { ctx =>
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

  override def resolveBy(id: AggregateIdType): DSL[Option[AggregateType]] =
    withContext[Option[AggregateType]] { ctx =>
      implicit val ec = ctx.ec
      Future {
        convertToAggregate(dao.findById(id.value))
      }
    }

  override def deleteById(id: AggregateIdType): DSL[Unit] =
    withContext[Unit] { ctx =>
      implicit val ec = ctx.ec
      Future {
        dao.deleteById(id.value)
        ()
      }
    }

}
