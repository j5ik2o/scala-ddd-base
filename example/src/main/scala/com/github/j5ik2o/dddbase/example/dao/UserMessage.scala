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
