package com.github.j5ik2o.dddbase.memcached

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiReader
import com.github.j5ik2o.dddbase.memcached.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import monix.eval.Task

trait AggregateMultiReadFeature extends AggregateMultiReader[RIO] with AggregateBaseReadFeature {

  override def resolveMulti(ids: Seq[IdType]): RIO[Seq[AggregateType]] =
    ReaderT[Task, MemcachedConnection, Seq[AggregateType]] { con =>
      for {
        results    <- dao.getMulti(ids.map(_.value.toString)).run(con)
        aggregates <- Task.sequence(results.map(v => convertToAggregate(v._1)(con)))
      } yield aggregates
    }

}
