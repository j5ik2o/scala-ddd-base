package com.github.j5ik2o.dddbase.memory
import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateMultiWriter, NullStoreOption, StoreOption }
import monix.eval.Task

trait AggregateMultiWriteFeature extends AggregateMultiWriter[RIO] with AggregateBaseWriteFeature {

  override type SMO = StoreOption
  override val defaultStoreMultiOption = NullStoreOption

  override def storeMulti(aggregates: Seq[AggregateType], storeMultiOption: SMO): RIO[Long] =
    for {
      records <- Task.traverse(aggregates) { aggregate =>
        convertToRecord(aggregate)
      }
      result <- dao.setMulti(records)
    } yield result

}
