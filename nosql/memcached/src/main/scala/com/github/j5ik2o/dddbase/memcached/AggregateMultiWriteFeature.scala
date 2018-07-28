package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiWriter
import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateMultiWriteFeature extends AggregateMultiWriter[RIO] with AggregateBaseWriteFeature {

  override def storeMulti(aggregates: Seq[AggregateType]): RIO[Long] =
    ReaderT[Task, MemcachedConnection, Long] { con =>
      for {
        records <- Task.traverse(aggregates) { aggregate =>
          convertToRecord(aggregate)(con)
        }
        result <- dao.setMulti(records, expireDuration).run(con)
      } yield result
    }

}
