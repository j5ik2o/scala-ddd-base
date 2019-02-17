package com.github.j5ik2o.dddbase.example.dao

package slick {
  import com.github.j5ik2o.dddbase.slick.SlickDaoSupport

  trait UserMessageComponent extends SlickDaoSupport {
    import profile.api._

    case class UserMessageRecord(
        messageId: Long,
        userId: Long,
        status: String,
        message: String,
        createdAt: java.time.ZonedDateTime,
        updatedAt: Option[java.time.ZonedDateTime]
    ) extends SoftDeletableRecord

    case class UserMessages(tag: Tag)
        extends TableBase[UserMessageRecord](tag, "user_message")
        with SoftDeletableTableSupport[UserMessageRecord] {
      def messageId: Rep[Long]                            = column[Long]("message_id")
      def userId: Rep[Long]                               = column[Long]("user_id")
      def status: Rep[String]                             = column[String]("status")
      def message: Rep[String]                            = column[String]("message")
      def createdAt: Rep[java.time.ZonedDateTime]         = column[java.time.ZonedDateTime]("created_at")
      def updatedAt: Rep[Option[java.time.ZonedDateTime]] = column[Option[java.time.ZonedDateTime]]("updated_at")
      def pk                                              = primaryKey("pk", (messageId, userId))
      override def * =
        (messageId, userId, status, message, createdAt, updatedAt) <> (UserMessageRecord.tupled, UserMessageRecord.unapply)
    }

    object UserMessageDao extends TableQuery(UserMessages)

  }

}

package skinny {

  import com.github.j5ik2o.dddbase.skinny.SkinnyDaoSupport
  import scalikejdbc._
  import _root_.skinny.orm._

  trait UserMessageComponent extends SkinnyDaoSupport {

    case class UserMessageRecordId(messageId: Long, userId: Long)

    case class UserMessageRecord(
        messageId: Long,
        userId: Long,
        status: String,
        message: String,
        createdAt: java.time.ZonedDateTime,
        updatedAt: Option[java.time.ZonedDateTime]
    ) extends Record[UserMessageRecordId] {
      override val id: UserMessageRecordId = UserMessageRecordId(messageId, userId)
    }

    object UserMessageDao extends DaoWithCompositeId[UserMessageRecordId, UserMessageRecord] {

//import ParameterBinderFactory._

      override val tableName: String = "user_message"

      override protected def toNamedIds(
          record: UserMessageRecord
      ): Seq[(Symbol, Any)] = Seq(
        'messageId -> record.id.messageId,
        'userId    -> record.id.userId
      )

      override protected def toNamedValues(record: UserMessageRecord): Seq[(Symbol, Any)] = Seq(
        'status     -> record.status,
        'message    -> record.message,
        'created_at -> record.createdAt,
        'updated_at -> record.updatedAt
      )

      override def defaultAlias: Alias[UserMessageRecord] = createAlias("u")

      override def extract(rs: WrappedResultSet, s: ResultName[UserMessageRecord]): UserMessageRecord =
        autoConstruct(rs, s)

      override protected def byCondition(id: UserMessageRecordId): scalikejdbc.SQLSyntax =
        sqls.eq(column.messageId, id.messageId).and.eq(column.userId, id.userId)

    }

  }

}
