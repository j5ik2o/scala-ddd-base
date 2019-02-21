package com.github.j5ik2o.dddbase.memory

import com.github.j5ik2o.dddbase.AggregateIO
import monix.eval.Task

trait AggregateIOBaseFeature extends AggregateIO[Task] {
  type RecordType <: MemoryDaoSupport#Record
  type DaoType <: MemoryDaoSupport#Dao[Task, RecordType]

  protected val dao: DaoType
}
