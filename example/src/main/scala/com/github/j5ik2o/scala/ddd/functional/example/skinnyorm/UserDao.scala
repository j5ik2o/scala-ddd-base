package com.github.j5ik2o.scala.ddd.functional.example.skinnyorm

import scalikejdbc.{ autoConstruct, WrappedResultSet }
import skinny.orm.{ Alias, SkinnyCRUDMapperWithId }

object UserDao extends SkinnyCRUDMapperWithId[Long, UserRecord] {

  override def defaultAlias: Alias[UserRecord] = createAlias("u")

  override def idToRawValue(id: Long): Any = id

  override def rawValueToId(value: Any): Long = value.asInstanceOf[Long]

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[UserRecord]): UserRecord = autoConstruct(rs, n)

}
