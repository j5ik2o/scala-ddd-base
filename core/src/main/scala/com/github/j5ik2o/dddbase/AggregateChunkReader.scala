package com.github.j5ik2o.dddbase

trait AggregateChunkReader[M[_]] extends AggregateIO[M] {

  def resolveMultiWithOffsetLimit(offset: Option[Long] = None, limit: Long = 100L): M[AggregatesChunk[AggregateType]]

}
