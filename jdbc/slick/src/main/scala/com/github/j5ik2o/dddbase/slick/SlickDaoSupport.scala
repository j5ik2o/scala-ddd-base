package com.github.j5ik2o.dddbase.slick

import java.time.{ Instant, ZoneId, ZonedDateTime }

trait SlickDaoSupport {

  val profile: slick.jdbc.JdbcProfile

  import profile.api._

  implicit val zonedDateTimeColumnType =
    MappedColumnType.base[ZonedDateTime, java.sql.Timestamp](
      { zdt =>
        new java.sql.Timestamp(zdt.toInstant.toEpochMilli)
      }, { ts =>
        val instant = Instant.ofEpochMilli(ts.getTime)
        ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
      }
    )

  trait Record

  trait SoftDeletableRecord extends Record {
    val status: String
  }

  abstract class TableBase[T](_tableTag: Tag, _tableName: String, _schemaName: Option[String] = None)
      extends Table[T](_tableTag, _schemaName, _tableName)

  trait SoftDeletableTableSupport[T] { this: TableBase[T] =>
    def status: Rep[String]
  }
}
