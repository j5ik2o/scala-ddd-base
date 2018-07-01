package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {
  import profile.api._

  protected def convertToAggregate(record: RecordType): RIO[AggregateType]

  protected def byCondition(id: IdType): TableType => Rep[Boolean] = _.id === id.value

  protected def byConditions(ids: Seq[IdType]): TableType => Rep[Boolean] = _.id.inSet(ids.map(_.value))

}
