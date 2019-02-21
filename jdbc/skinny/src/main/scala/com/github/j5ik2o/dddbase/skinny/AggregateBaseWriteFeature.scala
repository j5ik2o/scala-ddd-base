package com.github.j5ik2o.dddbase.skinny
import cats.data.ReaderT
import monix.eval.Task
import scalikejdbc.DBSession

trait AggregateBaseWriteFeature extends AggregateIOBaseFeature {

  protected def convertToRecord: AggregateType => ReaderT[Task, DBSession, RecordType]

}
