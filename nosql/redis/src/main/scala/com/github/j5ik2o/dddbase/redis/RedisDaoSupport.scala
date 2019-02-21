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

  trait Dao[M[_], R <: Record] {

    implicit val system: ActorSystem

    protected lazy val redisClient = RedisClient()

    def set(record: R, expire: Duration): M[Long]

    def setMulti(records: Seq[R], expire: Duration): M[Long]

    def get(id: String): M[Option[R]]

    def getMulti(ids: Seq[String]): M[Seq[R]]

    def delete(id: String): M[Long]

    def deleteMulti(ids: Seq[String]): M[Long]

  }

  trait DaoSoftDeletable[M[_], R <: SoftDeletableRecord] { this: Dao[M, R] =>

    def softDelete(id: String): M[Long]

    def softDeleteMulti(ids: Seq[String]): M[Long]

  }

}
