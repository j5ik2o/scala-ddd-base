package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{AggregateNotFoundException, AggregateSingleReader}
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateSingleReadFeature extends AggregateSingleReader[RIO] with AggregateBaseReadFeature {

  override def resolveById(id: IdType): RIO[AggregateType] =
    for {
      record <- ReaderT[Task, MemcachedConnection, RecordType] { con =>
        dao.get(id.value.toString).run(con).flatMap {
          case Some(v) =>
            Task.pure(v._1)
          case None =>
            Task.raiseError(AggregateNotFoundException(id))
        }
      }
      aggregate <- convertToAggregate(record)
    } yield aggregate

}
