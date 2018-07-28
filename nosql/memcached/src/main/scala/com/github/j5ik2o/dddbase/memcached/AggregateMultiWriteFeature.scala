package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiWriter
import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

import scala.concurrent.duration.Duration

trait AggregateMultiWriteFeature extends AggregateMultiWriter[RIO] with AggregateBaseWriteFeature {

  override type SMO = Duration
  override val defaultStoreMultiOption: Duration = Duration.Inf

  override def storeMulti(aggregates: Seq[AggregateType], storeMultiOption: SMO): RIO[Long] =
    ReaderT[Task, MemcachedConnection, Long] { con =>
      for {
        records <- Task.traverse(aggregates) { aggregate =>
          convertToRecord(aggregate)(con)
        }
        result <- dao.setMulti(records, storeMultiOption).run(con)
      } yield result
    }

}
