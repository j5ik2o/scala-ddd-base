package com.github.j5ik2o.dddbase.redis

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{AggregateNotFoundException, AggregateSingleReader}
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateSingleReadFeature extends AggregateSingleReader[RIO] with AggregateBaseReadFeature {

  override def resolveById(id: IdType): RIO[AggregateType] =
    for {
      record <- ReaderT[Task, RedisConnection, RecordType] { con =>
        dao.get(id.value.toString).run(con).flatMap {
          case Some(v) =>
            Task.pure(v)
          case None =>
            Task.raiseError(AggregateNotFoundException(id))
        }
      }
      aggregate <- convertToAggregate(record)
    } yield aggregate

}
