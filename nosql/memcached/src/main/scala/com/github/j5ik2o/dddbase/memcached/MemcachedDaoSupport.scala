package com.github.j5ik2o.dddbase.memcached

import akka.actor.ActorSystem
import cats.data.ReaderT
import com.github.j5ik2o.reactive.memcached.{ MemcachedClient, MemcachedConnection }
import monix.eval.Task

import scala.concurrent.duration.Duration

trait MemcachedDaoSupport {

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

    protected lazy val memcachedClient = MemcachedClient()

    def set(record: R, expire: Duration): ReaderT[Task, MemcachedConnection, Long]

    def setMulti(records: Seq[R], expire: Duration): ReaderT[Task, MemcachedConnection, Long]

    def get(id: String): ReaderT[Task, MemcachedConnection, Option[(R, Duration)]]

    def getMulti(ids: Seq[String]): ReaderT[Task, MemcachedConnection, Seq[(R, Duration)]]

    def delete(id: String): ReaderT[Task, MemcachedConnection, Long]

  }

  trait DaoSoftDeletable[R <: SoftDeletableRecord] { this: Dao[R] =>

    def softDelete(id: String): ReaderT[Task, MemcachedConnection, Long]

  }
}
