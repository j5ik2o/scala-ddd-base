package com.github.j5ik2o.dddbase.example.dao.memory

import java.time.{ Instant, ZoneId, ZonedDateTime }

import com.github.j5ik2o.dddbase.memory.MemoryDaoSupport
import com.google.common.cache.{ Cache, CacheBuilder }
import io.circe.{ Decoder, Encoder }
import monix.eval.Task

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration

trait UserAccountComponent extends MemoryDaoSupport {
  implicit val zonedDateTimeEncoder: Encoder[ZonedDateTime] = Encoder[Long].contramap(_.toInstant.toEpochMilli)
  implicit val zonedDateTimeDecoder: Decoder[ZonedDateTime] = Decoder[Long].map { ts =>
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault())
  }

  case class UserAccountRecord(id: String,
                               status: String,
                               email: String,
                               password: String,
                               firstName: String,
                               lastName: String,
                               createdAt: java.time.ZonedDateTime,
                               updatedAt: Option[java.time.ZonedDateTime])
      extends SoftDeletableRecord {
    override type This = UserAccountRecord
    override def withStatus(value: String): UserAccountRecord =
      copy(status = value)
  }

  case class UserAccountDao(minSize: Option[Int] = None,
                            maxSize: Option[Int] = None,
                            expireDuration: Option[Duration] = None,
                            concurrencyLevel: Option[Int] = None,
                            maxWeight: Option[Int] = None)
      extends Dao[UserAccountRecord]
      with DaoSoftDeletable[UserAccountRecord] {

    private val builder: CacheBuilder[AnyRef, AnyRef] = {
      val b  = CacheBuilder.newBuilder()
      val b0 = minSize.fold(b)(v => b.initialCapacity(v))
      val b1 = concurrencyLevel.fold(b0)(v => b0.concurrencyLevel(v))
      val b2 = maxSize.fold(b1)(v => b1.maximumSize(v))
      val b3 = expireDuration.fold(b2)(v => b2.expireAfterWrite(v.length, v.unit))
      val b4 = maxWeight.fold(b3)(v => b3.maximumWeight(v))
      b4
    }

    private val cache: Cache[String, UserAccountRecord] = builder.build()

    override def set(record: UserAccountRecord): Task[Long] =
      Task {
        cache.put(record.id, record)
        1L
      }
    override def setMulti(records: Seq[UserAccountRecord]): Task[Long] = Task {
      cache.putAll(records.map(v => (v.id, v)).toMap.asJava)
      records.size.toLong
    }

    override def get(
        id: String
    ): Task[Option[UserAccountRecord]] = Task {
      Option(cache.getIfPresent(id)).filterNot(_.status == DELETED)
    }
    override def getMulti(
        ids: Seq[String]
    ): Task[Seq[UserAccountRecord]] = Task {
      cache.getAllPresent(ids.asJava).asScala.values.filterNot(_.status == DELETED).toSeq
    }

    override def delete(id: String): Task[Long] = Task {
      cache.invalidate(id)
      1L
    }

    val DELETED = "deleted"

    override def softDelete(id: String): Task[Long] = get(id).flatMap {
      case Some(v) =>
        set(v.withStatus(DELETED))
      case None =>
        Task.pure(0L)
    }
  }

}
