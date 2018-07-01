package com.github.j5ik2o.dddbase.example.dao

import com.github.j5ik2o.dddbase.slick.SlickDaoSupport

trait UserAccountComponent extends SlickDaoSupport {

  import profile.api._

  case class UserAccountRecord(
      id: Long,
      status: String,
      email: String,
      password: String,
      firstName: String,
      lastName: String,
      createdAt: java.time.ZonedDateTime,
      updatedAt: Option[java.time.ZonedDateTime]
  ) extends SoftDeletableRecord

  case class UserAccounts(tag: Tag)
      extends TableBase[UserAccountRecord](tag, "user_account")
      with SoftDeletableTableSupport[UserAccountRecord] {
    // def id = column[Long]("id", O.PrimaryKey)
    def status    = column[String]("status")
    def email     = column[String]("email")
    def password  = column[String]("password")
    def firstName = column[String]("first_name")
    def lastName  = column[String]("last_name")
    def createdAt = column[java.time.ZonedDateTime]("created_at")
    def updatedAt = column[Option[java.time.ZonedDateTime]]("updated_at")
    override def * =
      (id, status, email, password, firstName, lastName, createdAt, updatedAt) <> (UserAccountRecord.tupled, UserAccountRecord.unapply)
  }

  object UserAccountDao extends TableQuery(UserAccounts)

}
