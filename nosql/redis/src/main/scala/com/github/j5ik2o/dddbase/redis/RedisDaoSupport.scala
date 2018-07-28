package com.github.j5ik2o.dddbase.redis

import akka.actor.ActorSystem
import cats.data.ReaderT
import com.github.j5ik2o.reactive.redis.{ RedisClient, RedisConnection }
import monix.eval.Task

import scala.concurrent.duration.Duration

trait RedisDaoSupport {

  trait Record {
    val id: String
  }

  trait SoftDeletableRecord extends Record {
    type This <: SoftDeletableRecord
    val status: String
    def withStatus(value: String): This
  }

  trait Dao[R <: Record] {

    implicit val system: ActorSystem

    protected lazy val redisClient = RedisClient()

    def set(record: R, expire: Duration): ReaderT[Task, RedisConnection, Long]

    def setMulti(records: Seq[(R, Duration)]): ReaderT[Task, RedisConnection, Long]

    def get(id: String): ReaderT[Task, RedisConnection, Option[R]]

    def getMulti(ids: Seq[String]): ReaderT[Task, RedisConnection, Seq[R]]

    def delete(id: String): ReaderT[Task, RedisConnection, Long]

  }

  trait DaoSoftDeletable[R <: SoftDeletableRecord] { this: Dao[R] =>

    def softDelete(id: String): ReaderT[Task, RedisConnection, Long]

  }

}
