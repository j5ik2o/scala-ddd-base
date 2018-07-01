package com.github.j5ik2o.dddbase.example.model

import enumeratum._

import scala.collection.immutable

sealed abstract class Status(override val entryName: String) extends EnumEntry

object Status extends Enum[Status] {
  override def values: immutable.IndexedSeq[Status] = findValues
  case object Active  extends Status("active")
  case object Suspend extends Status("suspend")
  case object Deleted extends Status("deleted")
}
