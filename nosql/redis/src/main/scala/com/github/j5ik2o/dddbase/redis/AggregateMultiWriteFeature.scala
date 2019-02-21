package com.github.j5ik2o.dddbase.redis

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiWriter
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateMultiWriteFeature
    extends AggregateMultiWriter[ReaderT[Task, RedisConnection, ?]]
    with AggregateBaseWriteFeature {

  override def storeMulti(aggregates: Seq[AggregateType]): ReaderT[Task, RedisConnection, Long] =
    ReaderT[Task, RedisConnection, Long] { con =>
      for {
        records <- Task.traverse(aggregates) { aggregate =>
          convertToRecord(aggregate)(con)
        }
        result <- dao.setMulti(records, expireDuration).run(con)
      } yield result
    }

}
