package com.github.j5ik2o.dddbase.redis
import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiWriter
import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateMultiWriteFeature extends AggregateMultiWriter[RIO] with AggregateBaseWriteFeature {

  override def storeMulti(aggregates: Seq[AggregateType]): RIO[Long] =
    ReaderT[Task, RedisConnection, Long] { con =>
      for {
        records <- Task.traverse(aggregates) { aggregate =>
          convertToRecord(aggregate)(con)
        }
        result <- dao.setMulti(records).run(con)
      } yield result
    }

}
