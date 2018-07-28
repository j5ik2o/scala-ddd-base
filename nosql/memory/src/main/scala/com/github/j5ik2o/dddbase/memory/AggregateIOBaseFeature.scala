package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.memory.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.{ AggregateIO, AggregateLongId }
import monix.eval.Task

trait AggregateIOBaseFeature extends AggregateIO[RIO] {
  override type IdType <: AggregateLongId
  type RecordType <: MemoryDaoSupport#Record
  type DaoType <: MemoryDaoSupport#Dao[RecordType]

  protected val dao: DaoType
}

object AggregateIOBaseFeature {
  type RIO[A] = Task[A]
}
