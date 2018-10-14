package com.github.j5ik2o.dddbase

case class AggregatesChunk[A <: Aggregate](index: Long, aggregates: Seq[A])
