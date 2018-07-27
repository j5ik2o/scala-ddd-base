package com.github.j5ik2o.dddbase.redis
import cats.data.ReaderT
import com.github.j5ik2o.dddbase.AggregateMultiReader
import com.github.j5ik2o.dddbase.redis.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task

trait AggregateMultiReadFeature extends AggregateMultiReader[RIO] with AggregateBaseReadFeature {

  override def resolveMulti(ids: Seq[IdType]): RIO[Seq[AggregateType]] =
    ReaderT[Task, RedisConnection, Seq[AggregateType]] { con =>
      for {
        results    <- dao.getMulti(ids.map(_.value.toString)).run(con)
        aggregates <- Task.sequence(results.map(convertToAggregate(_)(con)))
      } yield aggregates
    }

}
