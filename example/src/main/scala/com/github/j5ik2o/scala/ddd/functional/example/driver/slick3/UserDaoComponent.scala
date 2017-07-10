package com.github.j5ik2o.scala.ddd.functional.example.driver.slick3

import slick.lifted.ProvenShape

trait UserDaoComponent {

  val profile: slick.jdbc.JdbcProfile

  import profile.api._

  case class UserRecord(id: Long, name: String)

  case class UserDef(tag: Tag) extends Table[UserRecord](tag, "users") {
    def id = column[Long]("ID", O.PrimaryKey)

    def name = column[String]("NAME")

    override def * : ProvenShape[UserRecord] = (id, name) <> (UserRecord.tupled, UserRecord.unapply)
  }

  object UserDao extends TableQuery(UserDef)
}
