package com.github.j5ik2o.dddbase.example.dao

package slick {
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
      def id: Rep[Long]                                   = column[Long]("id")
      def status: Rep[String]                             = column[String]("status")
      def email: Rep[String]                              = column[String]("email")
      def password: Rep[String]                           = column[String]("password")
      def firstName: Rep[String]                          = column[String]("first_name")
      def lastName: Rep[String]                           = column[String]("last_name")
      def createdAt: Rep[java.time.ZonedDateTime]         = column[java.time.ZonedDateTime]("created_at")
      def updatedAt: Rep[Option[java.time.ZonedDateTime]] = column[Option[java.time.ZonedDateTime]]("updated_at")
      def pk                                              = primaryKey("pk", (id))
      override def * =
        (id, status, email, password, firstName, lastName, createdAt, updatedAt) <> (UserAccountRecord.tupled, UserAccountRecord.unapply)
    }

    object UserAccountDao extends TableQuery(UserAccounts)

  }

}

package skinny {

  import com.github.j5ik2o.dddbase.skinny.SkinnyDaoSupport
  import scalikejdbc._
  import _root_.skinny.orm._

  trait UserAccountComponent extends SkinnyDaoSupport {

    case class UserAccountRecord(
        id: Long,
        status: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        createdAt: java.time.ZonedDateTime,
        updatedAt: Option[java.time.ZonedDateTime]
    ) extends Record[Long]

    object UserAccountDao extends DaoWithId[Long, UserAccountRecord] {

      override implicit def pbf: ParameterBinderFactory[Long] = ParameterBinderFactory.longParameterBinderFactory

      override def useAutoIncrementPrimaryKey: Boolean = false

      override val tableName: String = "user_account"

      override protected def toNamedValues(record: UserAccountRecord): Seq[(Symbol, Any)] = Seq(
        'status     -> record.status,
        'email      -> record.email,
        'password   -> record.password,
        'first_name -> record.firstName,
        'last_name  -> record.lastName,
        'created_at -> record.createdAt,
        'updated_at -> record.updatedAt
      )

      override def defaultAlias: Alias[UserAccountRecord] = createAlias("u")

      override def extract(rs: WrappedResultSet, s: ResultName[UserAccountRecord]): UserAccountRecord =
        autoConstruct(rs, s)

      override def rawValueToId(value: Any): Long = value.toString.toLong

      override def idToRawValue(id: Long): Any = id

    }

  }

}
