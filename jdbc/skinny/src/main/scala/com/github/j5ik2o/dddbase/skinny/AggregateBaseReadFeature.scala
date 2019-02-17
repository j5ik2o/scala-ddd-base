package com.github.j5ik2o.dddbase.skinny

import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import scalikejdbc._

trait AggregateBaseReadFeature extends AggregateIOBaseFeature {

  protected def convertToAggregate: RecordType => RIO[AggregateType]

  protected def byCondition(id: IdType): SQLSyntax /*= sqls.eq(dao.defaultAlias.id, id.value)*/
  protected def byConditions(ids: Seq[IdType]): SQLSyntax /*= sqls.in(dao.defaultAlias.id, ids.map(_.value))*/

}
