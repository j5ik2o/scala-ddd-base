package com.github.j5ik2o.dddbase.skinny

import cats.data.ReaderT
import monix.eval.Task
import scalikejdbc._

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => ReaderT[Task, DBSession, AggregateType]

  protected def byCondition(id: IdType): SQLSyntax
  protected def byConditions(ids: Seq[IdType]): SQLSyntax

}
