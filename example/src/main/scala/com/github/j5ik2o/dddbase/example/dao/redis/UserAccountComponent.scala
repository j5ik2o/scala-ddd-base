package com.github.j5ik2o.dddbase.example.dao.redis
import java.time.{Instant, ZoneId, ZonedDateTime}

import akka.actor.ActorSystem
import cats.data.{NonEmptyList, ReaderT}
import com.github.j5ik2o.dddbase.redis.RedisDaoSupport
import com.github.j5ik2o.reactive.redis.{ReaderTTask, RedisConnection, Result}
import monix.eval.Task
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import io.circe.parser._
import cats.implicits._

import scala.concurrent.duration.{Duration, FiniteDuration}

trait UserAccountComponent extends RedisDaoSupport {

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

    val DELETED = "DELETED"

    private def internalSet(record: UserAccountRecord, expire: Duration): ReaderT[Task, RedisConnection, Result[Unit]] =
      if (expire.isFinite())
        redisClient.setEx(record.id,
                          FiniteDuration(expire._1, expire._2),
                          record.asJson.noSpaces.replaceAll("\"", "\\\\\""))
      else
        redisClient.set(record.id, record.asJson.noSpaces.replaceAll("\"", "\\\\\""))

    override def setMulti(
        records: Seq[(UserAccountRecord, Duration)]
    ): ReaderT[Task, RedisConnection, Long] = ReaderT { con =>
      Task
        .traverse(records) { record =>
          set(record._1, record._2).run(con)
        }
        .map(_.count(_ > 0))
    }

    override def set(
        record: UserAccountRecord,
        expire: Duration
    ): ReaderT[Task, RedisConnection, Long] = ReaderT { con =>
      internalSet(record, expire).run(con).map(_ => 1L)
    }

    override def getMulti(
        ids: Seq[String]
    ): ReaderT[Task, RedisConnection, Seq[UserAccountRecord]] = ReaderT { con =>
      Task
        .traverse(ids) { id =>
          get(id).run(con)
        }
        .map(_.foldLeft(Seq.empty[UserAccountRecord]) {
          case (result, e) =>
            result ++ e.map(Seq(_)).getOrElse(Seq.empty)
        })
    }

    private def internalGet(id: String): ReaderT[Task, RedisConnection, Option[UserAccountRecord]] =
      redisClient
        .get(id)
        .map {
          _.value.flatMap { v =>
            val r = parse(v).leftMap(error => new Exception(error.message)).flatMap { json =>
              json.as[UserAccountRecord].leftMap(error => new Exception(error.message)).map { v =>
                if (v.status == DELETED)
                  None
                else
                  Some(v)
              }
            }
            r match {
              case Right(v) =>
                v
              case Left(ex) =>
                throw ex
            }
          }
        }

    override def get(
        id: String
    ): ReaderT[Task, RedisConnection, Option[UserAccountRecord]] = ReaderT { con =>
      internalGet(id).run(con)
    }

    override def softDelete(id: String): ReaderT[Task, RedisConnection, Long] = {
//      for {
//        _    <- redisClient.multi()
//        uOpt <- internalGet(id)
//        r    <- uOpt.map(u => internalSet(u.withStatus(DELETED))).getOrElse(ReaderTTask.pure(0L))
//        e
//      } yield r

      get(id).flatMap {
        case Some(v) =>
          set(v.withStatus(DELETED), Duration.Inf)
        case None =>
          ReaderTTask.pure(0L)
      }
    }

    override def delete(
        id: String
    ): ReaderT[Task, RedisConnection, Long] = ReaderT { con =>
      redisClient.del(NonEmptyList.of(id)).run(con).map { _.value.toLong }
    }
  }

}
