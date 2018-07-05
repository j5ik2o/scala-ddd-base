package com.github.j5ik2o.dddbase.slick

import com.github.j5ik2o.dddbase.slick.AggregateIOBaseFeature.RIO
import slick.lifted.Rep

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => RIO[AggregateType]

  protected def byCondition(id: IdType): TableType => Rep[Boolean] = {
    import profile.api._
    _.id === id.value
  }

  protected def byConditions(ids: Seq[IdType]): TableType => Rep[Boolean] = {
    import profile.api._
    _.id.inSet(ids.map(_.value))
  }

}
