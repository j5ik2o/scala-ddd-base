package com.github.j5ik2o.dddbase.example.dao.memcached

import java.time.{ Instant, ZoneId, ZonedDateTime }

import akka.actor.ActorSystem
import cats.data.ReaderT
import cats.implicits._
import com.github.j5ik2o.dddbase.memcached.MemcachedDaoSupport
import com.github.j5ik2o.reactive.memcached.command.ValueDesc
import com.github.j5ik2o.reactive.memcached.{ MemcachedConnection, ReaderTTask }
import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{ Decoder, Encoder }
import monix.eval.Task

import scala.concurrent.duration.Duration

trait UserAccountComponent extends MemcachedDaoSupport {

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

  case class UserAccountDao()(implicit val system: ActorSystem)
      extends Dao[UserAccountRecord]
      with DaoSoftDeletable[UserAccountRecord] {

    val DELETED: String = "deleted"

    private def internalSet(record: UserAccountRecord, expire: Duration): ReaderT[Task, MemcachedConnection, Int] =
      memcachedClient.set(record.id, record.asJson.noSpaces.replaceAll("\"", "\\\\\""), expire)

    override def setMulti(
        records: Seq[UserAccountRecord],
        expire: Duration
    ): ReaderT[Task, MemcachedConnection, Long] = ReaderT { con =>
      Task
        .traverse(records) { record =>
          set(record, expire).run(con)
        }
        .map(_.count(_ > 0))
    }

    override def set(
        record: UserAccountRecord,
        expire: Duration
    ): ReaderT[Task, MemcachedConnection, Long] = ReaderT { con =>
      internalSet(record, expire).run(con).map(_ => 1L)
    }

    override def getMulti(
        ids: Seq[String]
    ): ReaderT[Task, MemcachedConnection, Seq[UserAccountRecord]] = ReaderT { con =>
      Task
        .traverse(ids) { id =>
          get(id).run(con)
        }
        .map(_.foldLeft(Seq.empty[UserAccountRecord]) {
          case (result, e) =>
            result ++ e.map(Seq(_)).getOrElse(Seq.empty)
        })
    }

    private def internalGet(id: String): ReaderT[Task, MemcachedConnection, Option[UserAccountRecord]] =
      memcachedClient
        .get(id)
        .map {
          _.flatMap { v: ValueDesc =>
            val r = parse(v.value.replaceAll("\\\\\"", "\"")).leftMap(error => new Exception(error.message)).flatMap {
              json =>
                json.as[UserAccountRecord].leftMap(error => new Exception(error.message)).map { e =>
                  if (e.status == DELETED)
                    None
                  else
                    Some(e)
                }
            }
            r match {
              case Right(v) => v
              case Left(ex) => throw ex
            }
          }
        }

    override def get(
        id: String
    ): ReaderT[Task, MemcachedConnection, Option[UserAccountRecord]] = ReaderT { con =>
      internalGet(id).run(con)
    }

    override def softDelete(id: String): ReaderT[Task, MemcachedConnection, Long] = {
      get(id).flatMap {
        case Some(v) =>
          set(v.withStatus(DELETED), Duration.Inf)
        case None =>
          ReaderTTask.pure(0L)
      }
    }

    override def softDeleteMulti(
        ids: Seq[String]
    ): ReaderT[Task, MemcachedConnection, Long] = ReaderT { con =>
      Task
        .traverse(ids) { id =>
          softDelete(id).run(con)
        }
        .map(_.count(_ > 0))
    }

    override def delete(
        id: String
    ): ReaderT[Task, MemcachedConnection, Long] = ReaderT { con =>
      memcachedClient.delete(id).run(con).map { v =>
        v.toLong
      }
    }

    override def deleteMulti(ids: Seq[String]): ReaderT[Task, MemcachedConnection, Long] = ReaderT { con =>
      Task
        .traverse(ids) { id =>
          delete(id).run(con)
        }
        .map(_.count(_ > 0))
    }
  }
}
